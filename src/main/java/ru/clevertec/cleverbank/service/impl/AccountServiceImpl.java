package ru.clevertec.cleverbank.service.impl;

import ru.clevertec.cleverbank.dao.AccountDAO;
import ru.clevertec.cleverbank.dao.impl.AccountDAOImpl;
import ru.clevertec.cleverbank.exception.notfound.AccountNotFoundException;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.service.AccountService;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;

    public AccountServiceImpl() {
        accountDAO = new AccountDAOImpl();
    }

    @Override
    public Account findById(String id) {
        return accountDAO.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " is not found!"));
    }

    @Override
    public Account updateBalance(Account account, BigDecimal balance) {
        account.setBalance(balance);
        return accountDAO.update(account);
    }

}
