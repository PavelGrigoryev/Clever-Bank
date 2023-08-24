package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.model.Account;

import java.math.BigDecimal;

public interface AccountService {

    Account findById(String id);

    Account updateBalanceById(String id, BigDecimal balance);

}
