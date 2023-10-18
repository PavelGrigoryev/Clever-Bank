package ru.clevertec.cleverbank.dao.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.builder.account.AccountTestBuilder;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class AccountDAOImplTest {

    @InjectMocks
    private AccountDAOImpl accountDAO;
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
            String sql = """
                    SELECT * FROM accounts a
                    JOIN banks b ON b.id = a.bank_id
                    JOIN users u ON u.id = a.user_id
                    WHERE a.id = ?
                    """;
            String id = "MU1Y 7LTU 7QLR 14XD 2789 T5MM XRXU";
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> accountDAO.findById(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = """
                    SELECT * FROM accounts a
                    JOIN banks b ON b.id = a.bank_id
                    JOIN users u ON u.id = a.user_id
                    WHERE a.id = ?
                    """;
            Account expected = AccountTestBuilder.aAccount().build();
            String id = expected.getId();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doNothing()
                    .when(preparedStatement)
                    .setString(1, id);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true)
                    .when(resultSet)
                    .next();
            getMockedAccountFromResultSet(expected);

            Optional<Account> account = accountDAO.findById(id);

            account.ifPresent(actual -> assertThat(actual).isEqualTo(expected));
        }

    }

    @Nested
    class FindAllTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = """
                    SELECT * FROM accounts a
                    JOIN banks b ON b.id = a.bank_id
                    JOIN users u ON u.id = a.user_id
                    """;
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> accountDAO.findAll());
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list of size one")
        void testShouldReturnListOfSizeOne() {
            String sql = """
                    SELECT * FROM accounts a
                    JOIN banks b ON b.id = a.bank_id
                    JOIN users u ON u.id = a.user_id
                    """;
            Account account = AccountTestBuilder.aAccount().build();
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
            getMockedAccountFromResultSet(account);

            List<Account> actual = accountDAO.findAll();

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list that contains expected response")
        void testShouldReturnListThatContainsExpectedResponse() {
            String sql = """
                    SELECT * FROM accounts a
                    JOIN banks b ON b.id = a.bank_id
                    JOIN users u ON u.id = a.user_id
                    """;
            Account expected = AccountTestBuilder.aAccount().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true, false)
                    .when(resultSet)
                    .next();
            getMockedAccountFromResultSet(expected);

            List<Account> actual = accountDAO.findAll();

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            String sql = """
                    SELECT * FROM accounts a
                    JOIN banks b ON b.id = a.bank_id
                    JOIN users u ON u.id = a.user_id
                    """;

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(false)
                    .when(resultSet)
                    .next();

            List<Account> actual = accountDAO.findAll();

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
                    INSERT INTO accounts (currency, balance, opening_date, closing_date, bank_id, user_id, id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
            Account account = AccountTestBuilder.aAccount().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> accountDAO.save(account));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = """
                    INSERT INTO accounts (currency, balance, opening_date, closing_date, bank_id, user_id, id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
            Account account = AccountTestBuilder.aAccount().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setMockedAccountInStatement(account);
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(account.getId())
                    .when(resultSet)
                    .getString(1);

            Account actual = accountDAO.save(account);

            assertThat(actual).isEqualTo(account);
        }

    }

    @Nested
    class UpdateTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = """
                    UPDATE accounts
                    SET currency = ?, balance = ?, opening_date = ?, closing_date = ?, bank_id = ?, user_id = ?
                    WHERE id = ?
                    """;
            Account account = AccountTestBuilder.aAccount().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> accountDAO.update(account));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = """
                    UPDATE accounts
                    SET currency = ?, balance = ?, opening_date = ?, closing_date = ?, bank_id = ?, user_id = ?
                    WHERE id = ?
                    """;
            Account account = AccountTestBuilder.aAccount().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setMockedAccountInStatement(account);
            doNothing()
                    .when(preparedStatement)
                    .setString(7, account.getId());
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(account.getId())
                    .when(resultSet)
                    .getString(1);

            Account actual = accountDAO.update(account);

            assertThat(actual).isEqualTo(account);
        }

    }

    @Nested
    class DeleteTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = "DELETE FROM accounts WHERE id = ?";
            String id = "MU1Y 7LTU 7QLR 14XD 2789 T5MM XRXU";
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> accountDAO.delete(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = "DELETE FROM accounts WHERE id = ?";
            String id = "MU1Y 7LTU 7QLR 14XD 2789 T5MM XRXU";

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            doNothing()
                    .when(preparedStatement)
                    .setString(1, id);
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(id)
                    .when(resultSet)
                    .getString("id");

            Optional<Account> actual = accountDAO.delete(id);

            actual.ifPresent(value -> assertThat(value.getId()).isEqualTo(id));
        }

    }

    private void setMockedAccountInStatement(Account account) throws SQLException {
        doNothing()
                .when(preparedStatement)
                .setString(1, String.valueOf(account.getCurrency()));
        doNothing()
                .when(preparedStatement)
                .setBigDecimal(2, account.getBalance());
        doNothing()
                .when(preparedStatement)
                .setObject(3, account.getOpeningDate());
        doNothing()
                .when(preparedStatement)
                .setObject(4, account.getClosingDate());
        doNothing()
                .when(preparedStatement)
                .setLong(5, account.getBank().getId());
        doNothing()
                .when(preparedStatement)
                .setLong(6, account.getUser().getId());
    }

    private void getMockedAccountFromResultSet(Account account) throws SQLException {
        doReturn(account.getId())
                .when(resultSet)
                .getString("id");
        doReturn(account.getCurrency().toString())
                .when(resultSet)
                .getString("currency");
        doReturn(account.getBalance())
                .when(resultSet)
                .getBigDecimal("balance");
        doReturn(Date.valueOf(account.getOpeningDate()))
                .when(resultSet)
                .getDate("opening_date");
        doReturn(null)
                .when(resultSet)
                .getDate("closing_date");

        doReturn(account.getBank().getId())
                .when(resultSet)
                .getLong("bank_id");
        doReturn(account.getBank().getName())
                .when(resultSet)
                .getString("name");
        doReturn(account.getBank().getAddress())
                .when(resultSet)
                .getString("address");
        doReturn(account.getBank().getPhoneNumber())
                .when(resultSet)
                .getString("phone_number");

        doReturn(account.getUser().getId())
                .when(resultSet)
                .getLong("user_id");
        doReturn(account.getUser().getLastname())
                .when(resultSet)
                .getString("lastname");
        doReturn(account.getUser().getFirstname())
                .when(resultSet)
                .getString("firstname");
        doReturn(account.getUser().getSurname())
                .when(resultSet)
                .getString("surname");
        doReturn(Date.valueOf(account.getUser().getRegisterDate()))
                .when(resultSet)
                .getDate("register_date");
        doReturn(account.getUser().getMobileNumber())
                .when(resultSet)
                .getString("mobile_number");
    }

}
