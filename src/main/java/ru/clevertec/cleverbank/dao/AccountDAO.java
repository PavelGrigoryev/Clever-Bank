package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDAO {

    Optional<Account> findById(String id);

    List<Account> findAll();

    Account save(Account account);

    Account update(Account account);

    Optional<Account> delete(String id);

}
