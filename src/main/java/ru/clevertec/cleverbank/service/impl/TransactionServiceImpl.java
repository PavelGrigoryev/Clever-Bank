package ru.clevertec.cleverbank.service.impl;

import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import ru.clevertec.cleverbank.dto.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.TransferBalanceResponse;
import ru.clevertec.cleverbank.exception.badrequest.AccountClosedException;
import ru.clevertec.cleverbank.exception.badrequest.BadCurrencyException;
import ru.clevertec.cleverbank.exception.badrequest.InsufficientFundsException;
import ru.clevertec.cleverbank.mapper.TransactionMapper;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.BankService;
import ru.clevertec.cleverbank.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionServiceImpl implements TransactionService {

    private final AccountService accountService;
    private final BankService bankService;
    private final TransactionDAO transactionDAO;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl() {
        accountService = new AccountServiceImpl();
        bankService = new BankServiceImpl();
        transactionDAO = new TransactionDAOImpl();
        transactionMapper = Mappers.getMapper(TransactionMapper.class);
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

        return transactionMapper.createChangeResponse(savedTransaction, updatedAccount.getCurrency(), oldBalance, newBalance);
    }

    @Override
    public TransferBalanceResponse transferBalance(TransferBalanceRequest request) {
        Account accountSender = accountService.findById(request.senderAccountId());
        checkAccountForClosingDate(accountSender.getClosingDate(), accountSender.getId());
        BigDecimal senderOldBalance = accountSender.getBalance();
        checkAccountForSufficientBalance(Type.TRANSFER, request.sum(), senderOldBalance);
        BigDecimal senderNewBalance = senderOldBalance.subtract(request.sum());
        Account updatedSenderAccount = accountService.updateBalance(accountSender, senderNewBalance);
        Bank bankSender = bankService.findById(accountSender.getBankId());

        Account accountRecipient = accountService.findById(request.recipientAccountId());
        checkAccountForClosingDate(accountRecipient.getClosingDate(), accountRecipient.getId());
        checkAccountForCurrency(accountSender.getCurrency(), accountRecipient.getCurrency());
        BigDecimal recipientOldBalance = accountRecipient.getBalance();
        BigDecimal recipientNewBalance = recipientOldBalance.add(request.sum());
        Account updatedRecipientAccount = accountService.updateBalance(accountRecipient, recipientNewBalance);
        Bank bankRecipient = bankService.findById(accountRecipient.getBankId());

        Transaction transaction = transactionMapper.createTransferTransaction(Type.TRANSFER, bankSender.getName(),
                bankRecipient.getName(), updatedSenderAccount.getId(), updatedRecipientAccount.getId(), request.sum());
        Transaction savedTransaction = transactionDAO.save(transaction);

        return transactionMapper.createTransferResponse(savedTransaction, accountSender.getCurrency(),
                senderOldBalance, senderNewBalance, recipientOldBalance, recipientNewBalance);
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
