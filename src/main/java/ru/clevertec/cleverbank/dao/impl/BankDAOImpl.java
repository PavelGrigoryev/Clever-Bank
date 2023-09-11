package ru.clevertec.cleverbank.dao.impl;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.jooq.impl.DSL;
import ru.clevertec.cleverbank.dao.BankDAO;
import ru.clevertec.cleverbank.exception.badrequest.UniquePhoneNumberException;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.tables.pojos.Bank;
import ru.clevertec.cleverbank.util.HikariConnectionManager;

import java.util.List;
import java.util.Optional;

import static ru.clevertec.cleverbank.Tables.ACCOUNT;
import static ru.clevertec.cleverbank.Tables.BANK;

@AllArgsConstructor
public class BankDAOImpl implements BankDAO {

    private final DSLContext dslContext;

    public BankDAOImpl() {
        dslContext = DSL.using(HikariConnectionManager.getConnection());
    }

    /**
     * Находит банк по его идентификатору в базе данных и возвращает его в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор банка
     * @return объект Optional, содержащий банк, если он найден, или пустой, если нет
     */
    @Override
    public Optional<Bank> findById(Long id) {
        return dslContext.fetchOptional(BANK, BANK.ID.eq(id))
                .map(bankRecord -> bankRecord.into(Bank.class));
    }

    /**
     * Находит все банки в базе данных и возвращает их в виде списка объектов Bank.
     *
     * @return список объектов Bank, представляющих банки
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public List<Bank> findAll() {
        return dslContext.selectFrom(BANK)
                .fetchInto(Bank.class);
    }

    /**
     * Сохраняет банк в базе данных и возвращает его в виде объекта Bank.
     *
     * @param bank объект Bank, представляющий банк для сохранения
     * @return объект Bank, представляющий сохраненный банк
     * @throws UniquePhoneNumberException если заданный телефон не уникальный
     */
    @Override
    public Bank save(Bank bank) {
        return dslContext.insertInto(BANK)
                .set(BANK.NAME, bank.getName())
                .set(BANK.ADDRESS, bank.getAddress())
                .set(BANK.PHONE_NUMBER, bank.getPhoneNumber())
                .onDuplicateKeyIgnore()
                .returning()
                .fetchOptional()
                .map(bankRecord -> bankRecord.into(Bank.class))
                .orElseThrow(() -> new UniquePhoneNumberException("Bank with phone number " + bank.getPhoneNumber()
                                                                  + " is already exist"));
    }

    /**
     * Обновляет банк в базе данных и возвращает его в виде объекта Bank.
     *
     * @param bank объект Bank, представляющий банк для обновления
     * @return объект Bank, представляющий обновленный банк
     * @throws UniquePhoneNumberException если заданный телефон не уникальный
     */
    @Override
    public Bank update(Bank bank) {
        Bank updated;
        try {
            updated = dslContext.update(BANK)
                    .set(BANK.NAME, bank.getName())
                    .set(BANK.ADDRESS, bank.getAddress())
                    .set(BANK.PHONE_NUMBER, bank.getPhoneNumber())
                    .where(BANK.ID.eq(bank.getId()))
                    .returning()
                    .fetchOptional()
                    .map(bankRecord -> bankRecord.into(Bank.class))
                    .orElseThrow(JDBCConnectionException::new);
        } catch (IntegrityConstraintViolationException e) {
            throw new UniquePhoneNumberException("Bank with phone number " + bank.getPhoneNumber() + " is already exist");
        }
        return updated;
    }

    /**
     * Удаляет банк из базы данных и связанные с ним счета по его id, и возвращает его в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор банка для удаления
     * @return объект Optional, содержащий удаленный банк, если он найден, или пустой, если нет
     */
    @Override
    public Optional<Bank> delete(Long id) {
        deleteAllBanksAccounts(id);
        return dslContext.deleteFrom(BANK)
                .where(BANK.ID.eq(id))
                .returning()
                .fetchOptional()
                .map(bankRecord -> bankRecord.into(Bank.class));
    }

    /**
     * Удаляет все счета, принадлежащие банку с заданным id, из базы данных.
     *
     * @param bankId Long, представляющее идентификатор банка, чьи счета нужно удалить
     */
    private void deleteAllBanksAccounts(Long bankId) {
        dslContext.deleteFrom(ACCOUNT)
                .where(ACCOUNT.BANK_ID.eq(bankId))
                .execute();
    }

}
