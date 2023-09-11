package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.AccountData;
import ru.clevertec.cleverbank.tables.pojos.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDAO {

    Optional<AccountData> findById(String id);

    List<Account> findAll();

    List<AccountData> findAllDatas();

    AccountData save(Account account);

    AccountData update(Account account);

    Optional<Account> delete(String id);

}
