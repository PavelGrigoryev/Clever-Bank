package ru.clevertec.cleverbank.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatement;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.util.HikariConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class TransactionDAOImpl implements TransactionDAO {

    private final Connection connection;

    public TransactionDAOImpl() {
        connection = HikariConnectionManager.getConnection();
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
        String sql = "SELECT * FROM transactions WHERE id = ?";
        Optional<Transaction> transaction = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    transaction = Optional.of(getTransactionFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return transaction;
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
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_sender_id = ?";
        return findAll(sql, id, transactions);
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
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_recipient_id = ?";
        return findAll(sql, id, transactions);
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
        String sql = """
                INSERT INTO transactions
                (date, time, type, bank_sender_id, bank_recipient_id, account_sender_id, account_recipient_id, sum_sender, sum_recipient)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setTransactionValuesInStatement(preparedStatement, transaction);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                transaction.setId(id);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return transaction;
    }

    /**
     * Находит все выписки транзакций в базе данных, выполненные в заданный период даты и в которых участвовал счёт с
     * заданным id, и возвращает их в виде списка объектов TransactionStatement.
     *
     * @param from LocalDate, представляющий начальную дату периода
     * @param to   LocalDate, представляющий конечную дату периода
     * @param id   String, представляющая идентификатор счета
     * @return список объектов TransactionStatement, представляющих выписки транзакций
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public List<TransactionStatement> findAllByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id) {
        List<TransactionStatement> statements = new ArrayList<>();
        String sql = """
                SELECT t.date, t.type, u.lastname, t.sum_sender, t.sum_recipient FROM transactions t
                JOIN accounts a ON t.account_sender_id = a.id
                JOIN accounts b ON t.account_recipient_id = b.id
                JOIN users u ON a.user_id = u.id
                WHERE date BETWEEN ? AND ?
                AND (account_sender_id = ? OR account_recipient_id = ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, from);
            preparedStatement.setObject(2, to);
            preparedStatement.setString(3, id);
            preparedStatement.setString(4, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    TransactionStatement statement = new TransactionStatement(
                            resultSet.getDate("date").toLocalDate(),
                            Type.valueOf(resultSet.getString("type")),
                            resultSet.getString("lastname"),
                            resultSet.getBigDecimal("sum_sender"),
                            resultSet.getBigDecimal("sum_recipient"));
                    statements.add(statement);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return statements;
    }

    /**
     * Находит сумму потраченных средств по всем транзакциям в базе данных, выполненные в заданный период даты
     * и в которых участвовал счёт с заданным id в качестве отправителя или получателя при выводе наличных.
     *
     * @param from LocalDate, представляющий начальную дату периода
     * @param to   LocalDate, представляющий конечную дату периода
     * @param id   String, представляющая идентификатор счета
     * @return BigDecimal, представляющий сумму потраченных средств
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public BigDecimal findSumOfSpentFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id) {
        BigDecimal spentFunds = BigDecimal.ZERO;
        String sql = """
                SELECT SUM(sum_sender) AS spent FROM transactions
                WHERE date BETWEEN ? AND ?
                AND ((account_sender_id = ? AND type IN ('TRANSFER', 'EXCHANGE'))
                OR (account_recipient_id = ? AND type = 'WITHDRAWAL'))
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, from);
            preparedStatement.setObject(2, to);
            preparedStatement.setString(3, id);
            preparedStatement.setString(4, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    spentFunds = resultSet.getBigDecimal("spent");
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return spentFunds;
    }

    /**
     * Находит сумму полученных средств по всем транзакциям в базе данных, выполненные в заданный период даты
     * и в которых участвовал счёт с заданным id в качестве получателя, кроме вывода наличных.
     *
     * @param from LocalDate, представляющий начальную дату периода
     * @param to   LocalDate, представляющий конечную дату периода
     * @param id   String, представляющая идентификатор счета
     * @return BigDecimal, представляющий сумму полученных средств
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public BigDecimal findSumOfReceivedFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id) {
        BigDecimal receivedFunds = BigDecimal.ZERO;
        String sql = """
                SELECT SUM(sum_recipient) AS received FROM transactions
                WHERE date BETWEEN ? AND ?
                AND (account_recipient_id = ? AND type != 'WITHDRAWAL')
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, from);
            preparedStatement.setObject(2, to);
            preparedStatement.setString(3, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    receivedFunds = resultSet.getBigDecimal("received");
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return receivedFunds;
    }

    /**
     * Выполняет общую логику поиска транзакций в базе данных по SQL-запросу и id счёта и возвращает их в виде
     * списка объектов Transaction.
     *
     * @param sql          String, представляющая SQL-запрос для поиска транзакций
     * @param id           String, представляющая идентификатор счета
     * @param transactions список объектов Transaction, в который будут добавлены найденные транзакции
     * @return список объектов Transaction, представляющих транзакции
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    private List<Transaction> findAll(String sql, String id, List<Transaction> transactions) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = getTransactionFromResultSet(resultSet);
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return transactions;
    }

    private Transaction getTransactionFromResultSet(ResultSet resultSet) throws SQLException {
        return Transaction.builder()
                .id(resultSet.getLong("id"))
                .date(resultSet.getDate("date").toLocalDate())
                .time(resultSet.getTime("time").toLocalTime())
                .type(Type.valueOf(resultSet.getString("type")))
                .bankSenderId(resultSet.getLong("bank_sender_id"))
                .bankRecipientId(resultSet.getLong("bank_recipient_id"))
                .accountSenderId(resultSet.getString("account_sender_id"))
                .accountRecipientId(resultSet.getString("account_recipient_id"))
                .sumSender(resultSet.getBigDecimal("sum_sender"))
                .sumRecipient(resultSet.getBigDecimal("sum_recipient"))
                .build();
    }

    private void setTransactionValuesInStatement(PreparedStatement preparedStatement, Transaction transaction) throws SQLException {
        preparedStatement.setObject(1, transaction.getDate());
        preparedStatement.setObject(2, transaction.getTime());
        preparedStatement.setString(3, String.valueOf(transaction.getType()));
        preparedStatement.setLong(4, transaction.getBankSenderId());
        preparedStatement.setLong(5, transaction.getBankRecipientId());
        preparedStatement.setString(6, transaction.getAccountSenderId());
        preparedStatement.setString(7, transaction.getAccountRecipientId());
        preparedStatement.setBigDecimal(8, transaction.getSumSender());
        preparedStatement.setBigDecimal(9, transaction.getSumRecipient());
    }

}
