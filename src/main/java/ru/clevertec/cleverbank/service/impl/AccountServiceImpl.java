package ru.clevertec.cleverbank.service.impl;

import lombok.AllArgsConstructor;
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
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.BankService;
import ru.clevertec.cleverbank.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
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

    /**
     * Реализует метод findById, который возвращает счёт по его id.
     *
     * @param id String, представляющая id счета
     * @return объект Account, представляющий счёт с заданным id
     * @throws AccountNotFoundException если счёт с заданным id не найден в базе данных
     */
    @Override
    public Account findById(String id) {
        return accountDAO.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " is not found!"));
    }

    /**
     * Реализует метод findByIdResponse, который возвращает ответ с данными о счёте по его id.
     *
     * @param id String, представляющая id счета
     * @return объект AccountResponse, представляющий ответ с данными о счёте с заданным id
     * @throws AccountNotFoundException если счёт с заданным id не найден в базе данных
     */
    @Override
    @ServiceLoggable
    public AccountResponse findByIdResponse(String id) {
        return accountMapper.toResponse(findById(id));
    }

    /**
     * Реализует метод findAll, который возвращает список всех счетов из базы данных.
     *
     * @return список объектов Account, представляющих все счета из базы данных
     */
    @Override
    public List<Account> findAll() {
        return accountDAO.findAll();
    }

    /**
     * Реализует метод findAllResponses, который возвращает список ответов со всеми данными о счетах из базы данных.
     *
     * @return список объектов AccountResponse, представляющих ответы со всеми данными о счетах из базы данных
     */
    @Override
    public List<AccountResponse> findAllResponses() {
        return accountMapper.toResponseList(findAll());
    }

    /**
     * Реализует метод save, который сохраняет новый счет в базу данных по данным из запроса.
     *
     * @param request объект AccountRequest, представляющий запрос с данными для создания нового счета
     * @return объект AccountResponse, представляющий ответ с данными о созданном счете
     */
    @Override
    @ServiceLoggable
    public AccountResponse save(AccountRequest request) {
        Account account = accountMapper.fromRequest(request);
        User user = userService.findById(request.userId());
        Bank bank = bankService.findById(request.bankId());
        account.setOpeningDate(LocalDate.now());
        account.setUser(user);
        account.setBank(bank);
        Account savedAccount = accountDAO.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    /**
     * Реализует метод updateBalance, который обновляет баланс счёта в базе данных по заданному значению.
     *
     * @param account объект Account, представляющий счёт, который нужно обновить
     * @param balance объект BigDecimal, представляющий новое значение баланса счёта
     * @return объект Account, представляющий обновленный счёт
     */
    @Override
    public Account updateBalance(Account account, BigDecimal balance) {
        account.setBalance(balance);
        return accountDAO.update(account);
    }

    /**
     * Реализует метод closeAccount, который закрывает счёт в базе данных по его идентификатору.
     *
     * @param id String, представляющая id счёта
     * @return объект AccountResponse, представляющий ответ с данными о закрытом счёте
     * @throws AccountNotFoundException если счёт с заданным id не найден в базе данных
     */
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

    /**
     * Реализует метод delete, который удаляет счёт из базы данных по его id.
     *
     * @param id String, представляющая id счёта
     * @return объект DeleteResponse, представляющий ответ с сообщением об успешном удалении счёта
     * @throws AccountNotFoundException если нет счёта с заданным id для удаления из базы данных
     */
    @Override
    @ServiceLoggable
    public DeleteResponse delete(String id) {
        return accountDAO.delete(id)
                .map(account -> new DeleteResponse("Account with ID " + id + " was successfully deleted"))
                .orElseThrow(() -> new AccountNotFoundException("No Account with ID " + id + " to delete"));
    }

}
