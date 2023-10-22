package ru.clevertec.cleverbank.dao.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatement;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.builder.transaction.TransactionStatementTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionTestBuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class TransactionDAOImplTest {

    @InjectMocks
    private TransactionDAOImpl transactionDAO;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @Nested
    class FindByIdTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = "SELECT * FROM transactions WHERE id = ?";
            long id = 1L;
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> transactionDAO.findById(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = "SELECT * FROM transactions WHERE id = ?";
            Transaction expected = TransactionTestBuilder.aTransaction().build();
            long id = expected.getId();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doNothing()
                    .when(preparedStatement)
                    .setLong(1, id);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true)
                    .when(resultSet)
                    .next();
            getMockedTransactionFromResultSet(expected);

            Optional<Transaction> transaction = transactionDAO.findById(id);

            transaction.ifPresent(actual -> assertThat(actual).isEqualTo(expected));
        }

    }

    @Nested
    class FindAllBySendersAccountIdTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = "SELECT * FROM transactions WHERE account_sender_id = ?";
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> transactionDAO.findAllBySendersAccountId(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list of size one")
        void testShouldReturnListOfSizeOne() {
            String sql = "SELECT * FROM transactions WHERE account_sender_id = ?";
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            int expectedSize = 1;

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true, false)
                    .when(resultSet)
                    .next();
            getMockedTransactionFromResultSet(transaction);

            List<Transaction> actual = transactionDAO.findAllBySendersAccountId(id);

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list that contains expected response")
        void testShouldReturnListThatContainsExpectedResponse() {
            String sql = "SELECT * FROM transactions WHERE account_sender_id = ?";
            Transaction expected = TransactionTestBuilder.aTransaction().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true, false)
                    .when(resultSet)
                    .next();
            getMockedTransactionFromResultSet(expected);

            List<Transaction> actual = transactionDAO.findAllBySendersAccountId(id);

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            String sql = "SELECT * FROM transactions WHERE account_sender_id = ?";
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(false)
                    .when(resultSet)
                    .next();

            List<Transaction> actual = transactionDAO.findAllBySendersAccountId(id);

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class FindAllByRecipientAccountIdTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = "SELECT * FROM transactions WHERE account_recipient_id = ?";
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> transactionDAO.findAllByRecipientAccountId(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list of size one")
        void testShouldReturnListOfSizeOne() {
            String sql = "SELECT * FROM transactions WHERE account_recipient_id = ?";
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            int expectedSize = 1;

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true, false)
                    .when(resultSet)
                    .next();
            getMockedTransactionFromResultSet(transaction);

            List<Transaction> actual = transactionDAO.findAllByRecipientAccountId(id);

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list that contains expected response")
        void testShouldReturnListThatContainsExpectedResponse() {
            String sql = "SELECT * FROM transactions WHERE account_recipient_id = ?";
            Transaction expected = TransactionTestBuilder.aTransaction().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true, false)
                    .when(resultSet)
                    .next();
            getMockedTransactionFromResultSet(expected);

            List<Transaction> actual = transactionDAO.findAllByRecipientAccountId(id);

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            String sql = "SELECT * FROM transactions WHERE account_recipient_id = ?";
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(false)
                    .when(resultSet)
                    .next();

            List<Transaction> actual = transactionDAO.findAllByRecipientAccountId(id);

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class SaveTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = """
                    INSERT INTO transactions
                    (date, time, type, bank_sender_id, bank_recipient_id, account_sender_id, account_recipient_id, sum_sender, sum_recipient)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> transactionDAO.save(transaction));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = """
                    INSERT INTO transactions
                    (date, time, type, bank_sender_id, bank_recipient_id, account_sender_id, account_recipient_id, sum_sender, sum_recipient)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            Transaction transaction = TransactionTestBuilder.aTransaction().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setMockedTransactionInStatement(transaction);
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(transaction.getId())
                    .when(resultSet)
                    .getLong(1);

            Transaction actual = transactionDAO.save(transaction);

            assertThat(actual).isEqualTo(transaction);
        }

    }

    @Nested
    class FindAllByPeriodOfDateAndAccountIdTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = """
                    SELECT t.date, t.type, u.lastname, t.sum_sender, t.sum_recipient FROM transactions t
                    JOIN accounts a ON t.account_sender_id = a.id
                    JOIN accounts b ON t.account_recipient_id = b.id
                    JOIN users u ON a.user_id = u.id
                    WHERE date BETWEEN ? AND ?
                    AND (account_sender_id = ? OR account_recipient_id = ?)
                    """;
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class,
                    () -> transactionDAO.findAllByPeriodOfDateAndAccountId(from, to, id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list of size one")
        void testShouldReturnListOfSizeOne() {
            String sql = """
                    SELECT t.date, t.type, u.lastname, t.sum_sender, t.sum_recipient FROM transactions t
                    JOIN accounts a ON t.account_sender_id = a.id
                    JOIN accounts b ON t.account_recipient_id = b.id
                    JOIN users u ON a.user_id = u.id
                    WHERE date BETWEEN ? AND ?
                    AND (account_sender_id = ? OR account_recipient_id = ?)
                    """;
            TransactionStatement statement = TransactionStatementTestBuilder.aTransactionStatement().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);
            int expectedSize = 1;

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            setMockedDatesAndIdInStatement(from, to, id);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true, false)
                    .when(resultSet)
                    .next();
            getMockedTransactionStatementFromResultSet(statement);

            List<TransactionStatement> actual = transactionDAO.findAllByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list that contains expected response")
        void testShouldReturnListThatContainsExpectedResponse() {
            String sql = """
                    SELECT t.date, t.type, u.lastname, t.sum_sender, t.sum_recipient FROM transactions t
                    JOIN accounts a ON t.account_sender_id = a.id
                    JOIN accounts b ON t.account_recipient_id = b.id
                    JOIN users u ON a.user_id = u.id
                    WHERE date BETWEEN ? AND ?
                    AND (account_sender_id = ? OR account_recipient_id = ?)
                    """;
            TransactionStatement expected = TransactionStatementTestBuilder.aTransactionStatement().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            setMockedDatesAndIdInStatement(from, to, id);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true, false)
                    .when(resultSet)
                    .next();
            getMockedTransactionStatementFromResultSet(expected);

            List<TransactionStatement> actual = transactionDAO.findAllByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            String sql = """
                    SELECT t.date, t.type, u.lastname, t.sum_sender, t.sum_recipient FROM transactions t
                    JOIN accounts a ON t.account_sender_id = a.id
                    JOIN accounts b ON t.account_recipient_id = b.id
                    JOIN users u ON a.user_id = u.id
                    WHERE date BETWEEN ? AND ?
                    AND (account_sender_id = ? OR account_recipient_id = ?)
                    """;
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            setMockedDatesAndIdInStatement(from, to, id);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(false)
                    .when(resultSet)
                    .next();

            List<TransactionStatement> actual = transactionDAO.findAllByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class FindSumOfSpentFundsByPeriodOfDateAndAccountIdTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = """
                    SELECT SUM(sum_sender) AS spent FROM transactions
                    WHERE date BETWEEN ? AND ?
                    AND ((account_sender_id = ? AND type IN ('TRANSFER', 'EXCHANGE'))
                    OR (account_recipient_id = ? AND type = 'WITHDRAWAL'))
                    """;
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class,
                    () -> transactionDAO.findSumOfSpentFundsByPeriodOfDateAndAccountId(from, to, id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = """
                    SELECT SUM(sum_sender) AS spent FROM transactions
                    WHERE date BETWEEN ? AND ?
                    AND ((account_sender_id = ? AND type IN ('TRANSFER', 'EXCHANGE'))
                    OR (account_recipient_id = ? AND type = 'WITHDRAWAL'))
                    """;
            BigDecimal expected = BigDecimal.TEN;
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            setMockedDatesAndIdInStatement(from, to, id);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(expected)
                    .when(resultSet)
                    .getBigDecimal("spent");

            BigDecimal actual = transactionDAO.findSumOfSpentFundsByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindSumOfReceivedFundsByPeriodOfDateAndAccountIdTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = """
                    SELECT SUM(sum_recipient) AS received FROM transactions
                    WHERE date BETWEEN ? AND ?
                    AND (account_recipient_id = ? AND type != 'WITHDRAWAL')
                    """;
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class,
                    () -> transactionDAO.findSumOfReceivedFundsByPeriodOfDateAndAccountId(from, to, id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = """
                    SELECT SUM(sum_recipient) AS received FROM transactions
                    WHERE date BETWEEN ? AND ?
                    AND (account_recipient_id = ? AND type != 'WITHDRAWAL')
                    """;
            BigDecimal expected = BigDecimal.TEN;
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doNothing()
                    .when(preparedStatement)
                    .setObject(1, from);
            doNothing()
                    .when(preparedStatement)
                    .setObject(2, to);
            doNothing()
                    .when(preparedStatement)
                    .setString(3, id);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(expected)
                    .when(resultSet)
                    .getBigDecimal("received");

            BigDecimal actual = transactionDAO.findSumOfReceivedFundsByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    private void setMockedDatesAndIdInStatement(LocalDate from, LocalDate to, String id) throws SQLException {
        doNothing()
                .when(preparedStatement)
                .setObject(1, from);
        doNothing()
                .when(preparedStatement)
                .setObject(2, to);
        doNothing()
                .when(preparedStatement)
                .setString(3, id);
        doNothing()
                .when(preparedStatement)
                .setString(4, id);
    }

    private void getMockedTransactionStatementFromResultSet(TransactionStatement statement) throws SQLException {
        doReturn(Date.valueOf(statement.date()))
                .when(resultSet)
                .getDate("date");
        doReturn(statement.type().toString())
                .when(resultSet)
                .getString("type");
        doReturn(statement.userLastname())
                .when(resultSet)
                .getString("lastname");
        doReturn(statement.sumSender())
                .when(resultSet)
                .getBigDecimal("sum_sender");
        doReturn(statement.sumRecipient())
                .when(resultSet)
                .getBigDecimal("sum_recipient");
    }

    private void setMockedTransactionInStatement(Transaction transaction) throws SQLException {
        doNothing()
                .when(preparedStatement)
                .setObject(1, transaction.getDate());
        doNothing()
                .when(preparedStatement)
                .setObject(2, transaction.getTime());
        doNothing()
                .when(preparedStatement)
                .setString(3, String.valueOf(transaction.getType()));
        doNothing()
                .when(preparedStatement)
                .setLong(4, transaction.getBankSenderId());
        doNothing()
                .when(preparedStatement)
                .setLong(5, transaction.getBankRecipientId());
        doNothing()
                .when(preparedStatement)
                .setString(6, transaction.getAccountSenderId());
        doNothing()
                .when(preparedStatement)
                .setString(7, transaction.getAccountRecipientId());
        doNothing()
                .when(preparedStatement)
                .setBigDecimal(8, transaction.getSumSender());
        doNothing()
                .when(preparedStatement)
                .setBigDecimal(9, transaction.getSumRecipient());
    }

    private void getMockedTransactionFromResultSet(Transaction transaction) throws SQLException {
        doReturn(transaction.getId())
                .when(resultSet)
                .getLong("id");
        doReturn(Date.valueOf(transaction.getDate()))
                .when(resultSet)
                .getDate("date");
        doReturn(Time.valueOf(transaction.getTime()))
                .when(resultSet)
                .getTime("time");
        doReturn(transaction.getType().toString())
                .when(resultSet)
                .getString("type");
        doReturn(transaction.getBankSenderId())
                .when(resultSet)
                .getLong("bank_sender_id");
        doReturn(transaction.getBankRecipientId())
                .when(resultSet)
                .getLong("bank_recipient_id");
        doReturn(transaction.getAccountSenderId())
                .when(resultSet)
                .getString("account_sender_id");
        doReturn(transaction.getAccountRecipientId())
                .when(resultSet)
                .getString("account_recipient_id");
        doReturn(transaction.getSumSender())
                .when(resultSet)
                .getBigDecimal("sum_sender");
        doReturn(transaction.getSumRecipient())
                .when(resultSet)
                .getBigDecimal("sum_recipient");
    }

}
