package ru.clevertec.cleverbank.service.impl;

import ru.clevertec.cleverbank.dao.AccountDAO;
import ru.clevertec.cleverbank.dao.BankDAO;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.dao.UserDAO;
import ru.clevertec.cleverbank.dao.impl.AccountDAOImpl;
import ru.clevertec.cleverbank.dao.impl.BankDAOImpl;
import ru.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import ru.clevertec.cleverbank.dao.impl.UserDAOImpl;
import ru.clevertec.cleverbank.dto.BalanceChangeRequest;
import ru.clevertec.cleverbank.dto.BalanceChangeResponse;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.AccountService;

import java.time.LocalDate;
import java.time.LocalTime;

public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;
    private final UserDAO userDAO;
    private final BankDAO bankDAO;
    private final TransactionDAO transactionDAO;

    public AccountServiceImpl() {
        accountDAO = new AccountDAOImpl();
        userDAO = new UserDAOImpl();
        bankDAO = new BankDAOImpl();
        transactionDAO = new TransactionDAOImpl();
    }

    @Override
    public BalanceChangeResponse replenish(BalanceChangeRequest request) {
        Account accountRecipient = accountDAO.findById(request.accountRecipientId())
                .orElseThrow(() -> new RuntimeException("!!!"));
        Bank bankRecipient = bankDAO.findById(accountRecipient.getBankId())
                .orElseThrow(() -> new RuntimeException("!!!"));

        Transaction transaction = Transaction.builder()
                .date(LocalDate.now())
                .time(LocalTime.now())
                .type(Type.REPLENISHMENT)
                .recipientsBank(bankRecipient.getName())
                .recipientsAccount(accountRecipient.getId())
                .sum(request.money())
                .build();

        Transaction savedTransaction = transactionDAO.save(transaction);

        return new BalanceChangeResponse(savedTransaction.getId(),
                savedTransaction.getDate(),
                savedTransaction.getTime(),
                savedTransaction.getType(),
                savedTransaction.getRecipientsBank(),
                savedTransaction.getRecipientsAccount(),
                savedTransaction.getSum(),
                accountRecipient.getBalance(),
                accountRecipient.getBalance().add(request.money()));
    }

    @Override
    public BalanceChangeResponse withdraw(BalanceChangeRequest request) {
        return null;
    }

}
