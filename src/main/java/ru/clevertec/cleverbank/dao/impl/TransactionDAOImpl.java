package ru.clevertec.cleverbank.dao.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.util.ConnectionManager;

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
public class TransactionDAOImpl implements TransactionDAO {

    private final Connection connection;

    public TransactionDAOImpl() {
        connection = ConnectionManager.getJDBCConnection();
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
        String sql = "SELECT * FROM transactions WHERE senders_account = ?";
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
        String sql = "SELECT * FROM transactions WHERE recipients_account = ?";
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
                (date, time, type, senders_bank, recipients_bank, senders_account, recipients_account, sum)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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
     * Находит все транзакции в базе данных, выполненные в заданный период даты и в которых участвовал счёт с
     * заданным id, и возвращает их в виде списка объектов Transaction.
     *
     * @param from LocalDate, представляющий начальную дату периода
     * @param to   LocalDate, представляющий конечную дату периода
     * @param id   String, представляющая идентификатор счета
     * @return список объектов Transaction, представляющих транзакции
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public List<Transaction> findAllByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = """
                SELECT * FROM transactions
                WHERE date BETWEEN ? AND ?
                AND (senders_account = ? OR recipients_account = ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, from);
            preparedStatement.setObject(2, to);
            preparedStatement.setString(3, id);
            preparedStatement.setString(4, id);
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
                SELECT SUM(sum) AS spent FROM transactions
                WHERE date BETWEEN ? AND ?
                AND ((senders_account = ? AND type = 'TRANSFER') OR (recipients_account = ? AND type = 'WITHDRAWAL'))
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
                SELECT SUM(sum) AS received FROM transactions
                WHERE date BETWEEN ? AND ?
                AND (recipients_account = ? AND type != 'WITHDRAWAL')
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
                .sendersBank(resultSet.getString("senders_bank"))
                .recipientsBank(resultSet.getString("recipients_bank"))
                .sendersAccount(resultSet.getString("senders_account"))
                .recipientsAccount(resultSet.getString("recipients_account"))
                .sum(new BigDecimal(resultSet.getString("sum")))
                .build();
    }

    private void setTransactionValuesInStatement(PreparedStatement preparedStatement, Transaction transaction) throws SQLException {
        preparedStatement.setObject(1, transaction.getDate());
        preparedStatement.setObject(2, transaction.getTime());
        preparedStatement.setString(3, String.valueOf(transaction.getType()));
        preparedStatement.setString(4, transaction.getSendersBank());
        preparedStatement.setString(5, transaction.getRecipientsBank());
        preparedStatement.setString(6, transaction.getSendersAccount());
        preparedStatement.setString(7, transaction.getRecipientsAccount());
        preparedStatement.setBigDecimal(8, transaction.getSum());
    }

}
