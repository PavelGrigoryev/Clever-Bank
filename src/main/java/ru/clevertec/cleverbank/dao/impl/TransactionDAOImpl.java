package ru.clevertec.cleverbank.dao.impl;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatement;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.tables.Account;
import ru.clevertec.cleverbank.tables.pojos.Transaction;
import ru.clevertec.cleverbank.util.HikariConnectionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.sum;
import static ru.clevertec.cleverbank.Tables.ACCOUNT;
import static ru.clevertec.cleverbank.Tables.TRANSACTION;
import static ru.clevertec.cleverbank.Tables.USER;

@AllArgsConstructor
public class TransactionDAOImpl implements TransactionDAO {

    private final DSLContext dslContext;

    public TransactionDAOImpl() {
        dslContext = DSL.using(HikariConnectionManager.getConnection());
    }

    /**
     * Находит транзакцию по ее id в базе данных и возвращает ее в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор транзакции
     * @return объект Optional, содержащий транзакцию, если она найдена, или пустой, если нет
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public Optional<Transaction> findById(Long id) {
        return dslContext.fetchOptional(TRANSACTION, TRANSACTION.ID.eq(id))
                .map(transactionRecord -> transactionRecord.into(Transaction.class));
    }

    /**
     * Находит все транзакции в базе данных, в которых участвовал счёт с заданным id в качестве отправителя,
     * и возвращает их в виде списка объектов Transaction.
     *
     * @param id String, представляющая идентификатор счёта-отправителя
     * @return список объектов Transaction, представляющих транзакции
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public List<Transaction> findAllBySendersAccountId(String id) {
        return dslContext.selectFrom(TRANSACTION)
                .where(TRANSACTION.ACCOUNT_SENDER_ID.eq(id))
                .fetchInto(Transaction.class);
    }

    /**
     * Находит все транзакции в базе данных, в которых участвовал счёт с заданным id в качестве получателя,
     * и возвращает их в виде списка объектов Transaction.
     *
     * @param id String, представляющая идентификатор счёта-получателя
     * @return список объектов Transaction, представляющих транзакции
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public List<Transaction> findAllByRecipientAccountId(String id) {
        return dslContext.selectFrom(TRANSACTION)
                .where(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(id))
                .fetchInto(Transaction.class);
    }

    /**
     * Сохраняет транзакцию в базе данных и возвращает ее в виде объекта Transaction.
     *
     * @param transaction объект Transaction, представляющий транзакцию для сохранения
     * @return объект Transaction, представляющий сохраненную транзакцию
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public Transaction save(Transaction transaction) {
        return dslContext.insertInto(TRANSACTION)
                .set(TRANSACTION.DATE, transaction.getDate())
                .set(TRANSACTION.TIME, transaction.getTime())
                .set(TRANSACTION.TYPE, transaction.getType())
                .set(TRANSACTION.BANK_SENDER_ID, transaction.getBankSenderId())
                .set(TRANSACTION.BANK_RECIPIENT_ID, transaction.getBankRecipientId())
                .set(TRANSACTION.ACCOUNT_SENDER_ID, transaction.getAccountSenderId())
                .set(TRANSACTION.ACCOUNT_RECIPIENT_ID, transaction.getAccountRecipientId())
                .set(TRANSACTION.SUM, transaction.getSum())
                .returning()
                .fetchOptional()
                .map(transactionRecord -> transactionRecord.into(Transaction.class))
                .orElseThrow(JDBCConnectionException::new);
    }

    /**
     * Находит все выписки транзакций в базе данных, выполненные в заданный период даты и в которых участвовал счёт с
     * заданным id, и возвращает их в виде списка объектов TransactionStatement.
     *
     * @param from LocalDate, представляющий начальную дату периода
     * @param to   LocalDate, представляющий конечную дату периода
     * @param id   String, представляющая идентификатор счета
     * @return список объектов TransactionStatement, представляющих выписки транзакций
     */
    @Override
    public List<TransactionStatement> findAllByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id) {
        Account a = ACCOUNT.as("a");
        Account b = ACCOUNT.as("b");
        return dslContext.select(TRANSACTION.DATE, TRANSACTION.TYPE, USER.LASTNAME, TRANSACTION.SUM)
                .from(TRANSACTION)
                .join(a).on(TRANSACTION.ACCOUNT_SENDER_ID.eq(a.ID))
                .join(b).on(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(b.ID))
                .join(USER).on(a.USER_ID.eq(USER.ID))
                .where(TRANSACTION.DATE.between(from).and(to))
                .and(TRANSACTION.ACCOUNT_SENDER_ID.eq(id).or(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(id)))
                .fetch()
                .map(r -> r.into(TransactionStatement.class));
    }

    /**
     * Находит сумму потраченных средств по всем транзакциям в базе данных, выполненные в заданный период даты
     * и в которых участвовал счёт с заданным id в качестве отправителя или получателя при выводе наличных.
     *
     * @param from LocalDate, представляющий начальную дату периода
     * @param to   LocalDate, представляющий конечную дату периода
     * @param id   String, представляющая идентификатор счета
     * @return BigDecimal, представляющий сумму потраченных средств
     */
    @Override
    public BigDecimal findSumOfSpentFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id) {
        return dslContext.select(sum(TRANSACTION.SUM).as("spent"))
                .from(TRANSACTION)
                .where(TRANSACTION.DATE.between(from).and(to))
                .and(TRANSACTION.ACCOUNT_SENDER_ID.eq(id).and(TRANSACTION.TYPE.eq(Type.TRANSFER.toString())))
                .or(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(id).and(TRANSACTION.TYPE.eq(Type.WITHDRAWAL.toString())))
                .fetchOptional()
                .map(r -> r.getValue("spent", BigDecimal.class))
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Находит сумму полученных средств по всем транзакциям в базе данных, выполненные в заданный период даты
     * и в которых участвовал счёт с заданным id в качестве получателя, кроме вывода наличных.
     *
     * @param from LocalDate, представляющий начальную дату периода
     * @param to   LocalDate, представляющий конечную дату периода
     * @param id   String, представляющая идентификатор счета
     * @return BigDecimal, представляющий сумму полученных средств
     */
    @Override
    public BigDecimal findSumOfReceivedFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id) {
        return dslContext.select(sum(TRANSACTION.SUM).as("received"))
                .from(TRANSACTION)
                .where(TRANSACTION.DATE.between(from).and(to))
                .and(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(id).and(TRANSACTION.TYPE.notEqual(Type.WITHDRAWAL.toString())))
                .fetchOptional()
                .map(r -> r.getValue("received", BigDecimal.class))
                .orElse(BigDecimal.ZERO);
    }

}
