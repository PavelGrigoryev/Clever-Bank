package ru.clevertec.cleverbank.service.impl;

import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import ru.clevertec.cleverbank.dto.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.TransactionResponse;
import ru.clevertec.cleverbank.dto.TransferBalanceRequest;
import ru.clevertec.cleverbank.exception.AccountClosedException;
import ru.clevertec.cleverbank.exception.InsufficientFundsException;
import ru.clevertec.cleverbank.mapper.TransactionMapper;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
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
    public TransactionResponse changeBalance(ChangeBalanceRequest request) {
        Account accountRecipient = accountService.findById(request.recipientAccountId());
        BigDecimal oldBalance = accountRecipient.getBalance();
        BigDecimal newBalance = checkAccount(accountRecipient.getClosingDate(), request, oldBalance);
        Account updatedAccount = accountService.updateBalance(accountRecipient, newBalance);

        Bank bankRecipient = bankService.findById(accountRecipient.getBankId());

        Transaction transaction = transactionMapper
                .createTransaction(request.type(), bankRecipient.getName(), updatedAccount.getId(), request.sum());
        Transaction savedTransaction = transactionDAO.save(transaction);

        return transactionMapper.createResponse(savedTransaction, oldBalance, newBalance);
    }

    @Override
    public TransactionResponse transferBalance(TransferBalanceRequest request) {
        Account accountSender = accountService.findById(request.senderAccountId());
        if (accountSender.getClosingDate() != null) {
            throw new AccountClosedException("Account with ID " + accountSender.getId()
                                             + " is closed since " + accountSender.getClosingDate());
        }
        Account accountRecipient = accountService.findById(request.recipientAccountId());
        if (accountRecipient.getClosingDate() != null) {
            throw new AccountClosedException("Account with ID " + accountRecipient.getId()
                                             + " is closed since " + accountRecipient.getClosingDate());
        }


        return null;
    }

    private static BigDecimal checkAccount(LocalDate closingDate, ChangeBalanceRequest request, BigDecimal oldBalance) {
        if (closingDate != null) {
            throw new AccountClosedException("Account with ID " + request.recipientAccountId()
                                             + " is closed since " + closingDate);
        }
        if (request.type() == Type.WITHDRAWAL && oldBalance.compareTo(request.sum()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the account! You want to withdrawal "
                                                 + request.sum() + " but you have only " + oldBalance);
        }
        return request.type() == Type.REPLENISHMENT
                ? oldBalance.add(request.sum())
                : oldBalance.subtract(request.sum());
    }

}
