package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account findById(String id);

    Account updateBalance(Account account, BigDecimal balance);

    List<Account> findAll();

}
