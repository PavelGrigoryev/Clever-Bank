package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.model.AccountData;
import ru.clevertec.cleverbank.tables.pojos.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    AccountData findById(String id);

    AccountResponse findByIdResponse(String id);

    List<Account> findAll();

    List<AccountResponse> findAllResponses();

    AccountResponse save(AccountRequest request);

    AccountData updateBalance(Account account, BigDecimal balance);

    AccountResponse closeAccount(String id);

    DeleteResponse delete(String id);

}
