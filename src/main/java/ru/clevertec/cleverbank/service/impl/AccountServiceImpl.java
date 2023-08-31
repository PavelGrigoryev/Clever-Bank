package ru.clevertec.cleverbank.service.impl;

import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.aspect.annotation.ServiceLoggable;
import ru.clevertec.cleverbank.dao.AccountDAO;
import ru.clevertec.cleverbank.dao.impl.AccountDAOImpl;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.exception.notfound.AccountNotFoundException;
import ru.clevertec.cleverbank.mapper.AccountMapper;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.BankService;
import ru.clevertec.cleverbank.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;
    private final UserService userService;
    private final BankService bankService;
    private final AccountMapper accountMapper;

    public AccountServiceImpl() {
        accountDAO = new AccountDAOImpl();
        userService = new UserServiceImpl();
        bankService = new BankServiceImpl();
        accountMapper = Mappers.getMapper(AccountMapper.class);
    }

    @Override
    public Account findById(String id) {
        return accountDAO.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " is not found!"));
    }

    @Override
    @ServiceLoggable
    public AccountResponse findByIdResponse(String id) {
        return accountMapper.toResponse(findById(id));
    }

    @Override
    public List<Account> findAll() {
        return accountDAO.findAll();
    }

    @Override
    public List<AccountResponse> findAllResponses() {
        return accountMapper.toResponseList(findAll());
    }

    @Override
    @ServiceLoggable
    public AccountResponse save(AccountRequest request) {
        Account account = accountMapper.fromRequest(request);
        userService.findById(account.getUserId());
        bankService.findById(account.getBankId());
        account.setOpeningDate(LocalDate.now());
        Account savedAccount = accountDAO.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    @Override
    public Account updateBalance(Account account, BigDecimal balance) {
        account.setBalance(balance);
        return accountDAO.update(account);
    }

    @Override
    @ServiceLoggable
    public AccountResponse closeAccount(String id) {
        Account account = accountDAO.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " is not found!"));
        account.setClosingDate(LocalDate.now());
        account.setBalance(BigDecimal.ZERO);
        Account updatedAccount = accountDAO.update(account);
        return accountMapper.toResponse(updatedAccount);
    }

    @Override
    @ServiceLoggable
    public DeleteResponse delete(String id) {
        return accountDAO.delete(id)
                .map(account -> new DeleteResponse("Account with ID " + id + " was successfully deleted"))
                .orElseThrow(() -> new AccountNotFoundException("No Account with ID " + id + " to delete"));
    }

}
