package ru.clevertec.cleverbank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.config.ConnectionManager;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import ru.clevertec.cleverbank.dto.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.TransferBalanceResponse;
import ru.clevertec.cleverbank.exception.badrequest.AccountClosedException;
import ru.clevertec.cleverbank.exception.badrequest.BadCurrencyException;
import ru.clevertec.cleverbank.exception.badrequest.InsufficientFundsException;
import ru.clevertec.cleverbank.exception.internalservererror.TransactionException;
import ru.clevertec.cleverbank.mapper.TransactionMapper;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.BankCheckService;
import ru.clevertec.cleverbank.service.BankService;
import ru.clevertec.cleverbank.service.TransactionService;
import ru.clevertec.cleverbank.service.UploadFileService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final AccountService accountService;
    private final BankService bankService;
    private final TransactionDAO transactionDAO;
    private final TransactionMapper transactionMapper;
    private final BankCheckService bankCheckService;
    private final UploadFileService uploadFileService;
    private final Connection connection;

    public TransactionServiceImpl() {
        accountService = new AccountServiceImpl();
        bankService = new BankServiceImpl();
        transactionDAO = new TransactionDAOImpl();
        transactionMapper = Mappers.getMapper(TransactionMapper.class);
        bankCheckService = new BankCheckServiceImpl();
        uploadFileService = new UploadFileServiceImpl();
        connection = ConnectionManager.getJDBCConnection();
    }

    @Override
    public ChangeBalanceResponse changeBalance(ChangeBalanceRequest request) {
        Account accountRecipient = accountService.findById(request.recipientAccountId());
        checkAccountForClosingDate(accountRecipient.getClosingDate(), accountRecipient.getId());

        BigDecimal oldBalance = accountRecipient.getBalance();
        checkAccountForSufficientBalance(request.type(), request.sum(), oldBalance);
        BigDecimal newBalance = request.type() == Type.REPLENISHMENT
                ? oldBalance.add(request.sum())
                : oldBalance.subtract(request.sum());
        Account updatedAccount = accountService.updateBalance(accountRecipient, newBalance);

        Bank bankRecipient = bankService.findById(accountRecipient.getBankId());

        Transaction transaction = transactionMapper
                .createChangeTransaction(request.type(), bankRecipient.getName(), updatedAccount.getId(), request.sum());
        Transaction savedTransaction = transactionDAO.save(transaction);

        ChangeBalanceResponse response = transactionMapper
                .createChangeResponse(savedTransaction, updatedAccount.getCurrency(), oldBalance, newBalance);
        String check = bankCheckService.createChangeBalanceCheck(response);
        uploadFileService.uploadCheck(check);
        log.info(check);
        return response;
    }

    @Override
    public TransferBalanceResponse transferBalance(TransferBalanceRequest request) throws SQLException {
        connection.setAutoCommit(false);
        try {
            Account accountSender = accountService.findById(request.senderAccountId());
            Account accountRecipient = accountService.findById(request.recipientAccountId());
            checkAccountForClosingDate(accountSender.getClosingDate(), accountSender.getId());
            checkAccountForClosingDate(accountRecipient.getClosingDate(), accountRecipient.getId());
            checkAccountForCurrency(accountSender.getCurrency(), accountRecipient.getCurrency());

            BigDecimal senderOldBalance = accountSender.getBalance();
            checkAccountForSufficientBalance(Type.TRANSFER, request.sum(), senderOldBalance);
            BigDecimal senderNewBalance = senderOldBalance.subtract(request.sum());
            Account updatedSenderAccount = accountService.updateBalance(accountSender, senderNewBalance);
            Bank bankSender = bankService.findById(accountSender.getBankId());

            BigDecimal recipientOldBalance = accountRecipient.getBalance();
            BigDecimal recipientNewBalance = recipientOldBalance.add(request.sum());
            Account updatedRecipientAccount = accountService.updateBalance(accountRecipient, recipientNewBalance);
            Bank bankRecipient = bankService.findById(accountRecipient.getBankId());

            Transaction transaction = transactionMapper.createTransferTransaction(Type.TRANSFER, bankSender.getName(),
                    bankRecipient.getName(), updatedSenderAccount.getId(), updatedRecipientAccount.getId(), request.sum());
            Transaction savedTransaction = transactionDAO.save(transaction);

            connection.commit();
            return transactionMapper.createTransferResponse(savedTransaction, accountSender.getCurrency(),
                    senderOldBalance, senderNewBalance, recipientOldBalance, recipientNewBalance);
        } catch (Exception e) {
            log.error(e.getMessage());
            connection.rollback();
            throw new TransactionException("Transaction rollback, cause: " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void checkAccountForClosingDate(LocalDate closingDate, String accountId) {
        if (closingDate != null) {
            throw new AccountClosedException("Account with ID " + accountId + " is closed since " + closingDate);
        }
    }

    private static void checkAccountForCurrency(Currency senderCurrency, Currency resipientCurrency) {
        if (!senderCurrency.equals(resipientCurrency)) {
            throw new BadCurrencyException("Your currency is " + resipientCurrency
                                           + ", but account currency is " + senderCurrency);
        }
    }

    private static void checkAccountForSufficientBalance(Type type, BigDecimal sum, BigDecimal oldBalance) {
        if (type != Type.REPLENISHMENT && oldBalance.compareTo(sum) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the account! You want to withdrawal/transfer "
                                                 + sum + ", but you have only " + oldBalance);
        }
    }

}
