package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.Account;

import java.util.Optional;

public interface AccountDAO {

    Optional<Account> findById(String id);

}
