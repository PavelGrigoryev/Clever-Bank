package ru.clevertec.cleverbank.dao.impl;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import ru.clevertec.cleverbank.dao.AccountDAO;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.AccountData;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.tables.pojos.Account;
import ru.clevertec.cleverbank.tables.pojos.Bank;
import ru.clevertec.cleverbank.tables.pojos.User;
import ru.clevertec.cleverbank.util.HikariConnectionManager;
import ru.clevertec.cleverbank.util.RandomStringGenerator;

import java.util.List;
import java.util.Optional;

import static ru.clevertec.cleverbank.Tables.ACCOUNT;
import static ru.clevertec.cleverbank.Tables.BANK;
import static ru.clevertec.cleverbank.Tables.USER;

@AllArgsConstructor
public class AccountDAOImpl implements AccountDAO {

    private final DSLContext dslContext;

    public AccountDAOImpl() {
        dslContext = DSL.using(HikariConnectionManager.getConnection());
    }

    /**
     * Находит счёт по его id и связанные с ним банк и юзера в базе данных и возвращает его в виде объекта Optional.
     *
     * @param id String, представляющая идентификатор счета
     * @return объект Optional, содержащий счет, если он найден, или пустой, если нет
     */
    @Override
    public Optional<AccountData> findById(String id) {
        return dslContext.select()
                .from(ACCOUNT)
                .join(BANK).on(BANK.ID.eq(ACCOUNT.BANK_ID))
                .join(USER).on(USER.ID.eq(ACCOUNT.USER_ID))
                .where(ACCOUNT.ID.eq(id))
                .fetchOptional()
                .map(this::getAccountWithBankAndUser);
    }

    /**
     * Находит все счета в базе данных и возвращает их в виде списка объектов Account.
     *
     * @return список объектов Account, представляющих счета
     */
    @Override
    public List<Account> findAll() {
        return dslContext.selectFrom(ACCOUNT)
                .fetchInto(Account.class);
    }

    /**
     * Находит все счета и связанные с ним банк и юзера в базе данных и возвращает их в виде списка объектов AccountData.
     *
     * @return список объектов AccountData, представляющих счета
     */
    @Override
    public List<AccountData> findAllDatas() {
        return dslContext.select()
                .from(ACCOUNT)
                .join(BANK).on(BANK.ID.eq(ACCOUNT.BANK_ID))
                .join(USER).on(USER.ID.eq(ACCOUNT.USER_ID))
                .fetch()
                .map(this::getAccountWithBankAndUser);
    }

    /**
     * Сохраняет счёт в базе данных и возвращает его в виде объекта AccountData.
     *
     * @param account объект Account, представляющий счёт для сохранения
     * @return объект AccountData, представляющий сохраненный счёт
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public AccountData save(Account account) {
        return dslContext.insertInto(ACCOUNT)
                .set(ACCOUNT.ID, RandomStringGenerator.generateRandomString())
                .set(ACCOUNT.CURRENCY, account.getCurrency())
                .set(ACCOUNT.BALANCE, account.getBalance())
                .set(ACCOUNT.OPENING_DATE, account.getOpeningDate())
                .set(ACCOUNT.CLOSING_DATE, account.getClosingDate())
                .set(ACCOUNT.BANK_ID, account.getBankId())
                .set(ACCOUNT.USER_ID, account.getUserId())
                .onDuplicateKeyIgnore()
                .returning()
                .fetchOptional()
                .map(accountRecord -> accountRecord.into(Account.class))
                .flatMap(acc -> findById(acc.getId()))
                .orElseThrow(JDBCConnectionException::new);
    }

    /**
     * Обновляет счёт в базе данных и возвращает его в виде объекта AccountData.
     *
     * @param account объект Account, представляющий счёт для обновления
     * @return объект AccountData, представляющий обновленный счёт
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public AccountData update(Account account) {
        return dslContext.update(ACCOUNT)
                .set(ACCOUNT.CURRENCY, account.getCurrency())
                .set(ACCOUNT.BALANCE, account.getBalance())
                .set(ACCOUNT.OPENING_DATE, account.getOpeningDate())
                .set(ACCOUNT.CLOSING_DATE, account.getClosingDate())
                .set(ACCOUNT.BANK_ID, account.getBankId())
                .set(ACCOUNT.USER_ID, account.getUserId())
                .where(ACCOUNT.ID.eq(account.getId()))
                .returning()
                .fetchOptional()
                .map(accountRecord -> accountRecord.into(Account.class))
                .flatMap(acc -> findById(acc.getId()))
                .orElseThrow(JDBCConnectionException::new);
    }

    /**
     * Удаляет счёт из базы данных по его id и возвращает его в виде объекта Optional.
     *
     * @param id String, представляющая идентификатор счёта для удаления
     * @return объект Optional, содержащий удаленный счет, если он найден, или пустой, если нет
     */
    @Override
    public Optional<Account> delete(String id) {
        return dslContext.deleteFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(id))
                .returning()
                .fetchOptional()
                .map(accountRecord -> accountRecord.into(Account.class));
    }

    private AccountData getAccountWithBankAndUser(org.jooq.Record r) {
        return AccountData.builder()
                .id(r.getValue(ACCOUNT.ID))
                .currency(Currency.valueOf(r.getValue(ACCOUNT.CURRENCY)))
                .balance(r.getValue(ACCOUNT.BALANCE))
                .openingDate(r.getValue(ACCOUNT.OPENING_DATE))
                .closingDate(r.getValue(ACCOUNT.CLOSING_DATE))
                .bank(new Bank(r.getValue(BANK.ID),
                        r.getValue(BANK.NAME),
                        r.getValue(BANK.ADDRESS),
                        r.getValue(BANK.PHONE_NUMBER)))
                .user(new User(r.getValue(USER.ID),
                        r.getValue(USER.LASTNAME),
                        r.getValue(USER.FIRSTNAME),
                        r.getValue(USER.SURNAME),
                        r.getValue(USER.REGISTER_DATE),
                        r.getValue(USER.MOBILE_NUMBER)))
                .build();
    }

}
