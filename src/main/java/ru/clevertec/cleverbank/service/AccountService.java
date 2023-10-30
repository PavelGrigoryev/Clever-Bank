package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account findById(String id);

    AccountResponse findByIdResponse(String id);

    List<Account> findAll();

    List<Account> findAllWithPositiveBalance();

    List<AccountResponse> findAllResponses();

    AccountResponse save(AccountRequest request);

    Account updateBalance(Account account, BigDecimal balance);

    AccountResponse closeAccount(String id);

    DeleteResponse delete(String id);

}
