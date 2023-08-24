package ru.clevertec.cleverbank.service.impl;

import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import ru.clevertec.cleverbank.dto.TransactionRequest;
import ru.clevertec.cleverbank.dto.TransactionResponse;
import ru.clevertec.cleverbank.mapper.TransactionMapper;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.BankService;
import ru.clevertec.cleverbank.service.TransactionService;

import java.math.BigDecimal;

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
    public TransactionResponse replenish(TransactionRequest request) {
        Account accountRecipient = accountService.findById(request.accountRecipientId());
        BigDecimal oldBalance = accountRecipient.getBalance();
        Account updatedAccount = accountService.updateBalanceById(accountRecipient.getId(), request.money());

        Bank bankRecipient = bankService.findById(accountRecipient.getBankId());

        Transaction transaction = transactionMapper
                .toReplenishTransaction(bankRecipient.getName(), updatedAccount.getId(), request.money());
        Transaction savedTransaction = transactionDAO.save(transaction);

        return transactionMapper.toReplenishResponse(savedTransaction, oldBalance, updatedAccount.getBalance());
    }

    @Override
    public TransactionResponse withdraw(TransactionRequest request) {
        return null;
    }

}
