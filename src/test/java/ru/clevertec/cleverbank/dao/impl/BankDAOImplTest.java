package ru.clevertec.cleverbank.dao.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.util.bank.BankTestBuilder;

import java.sql.Connection;
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
class BankDAOImplTest {

    @InjectMocks
    private BankDAOImpl bankDAO;
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
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = "SELECT * FROM banks WHERE id = ?";
            long id = 1L;
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> bankDAO.findById(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            String sql = "SELECT * FROM banks WHERE id = ?";
            Bank bank = BankTestBuilder.aBank().build();
            Optional<Bank> expected = Optional.of(bank);
            long id = bank.getId();

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
            getMockedBankFromResultSet(bank);

            Optional<Bank> actual = bankDAO.findById(id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindAllTest {

        @Test
        @SneakyThrows
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = "SELECT * FROM banks";
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> bankDAO.findAll());
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnListOfSizeOne() {
            String sql = "SELECT * FROM banks";
            Bank bank = BankTestBuilder.aBank().build();
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
            getMockedBankFromResultSet(bank);

            List<Bank> actual = bankDAO.findAll();

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        void testShouldReturnListThatContainsExpectedResponse() {
            String sql = "SELECT * FROM banks";
            Bank expected = BankTestBuilder.aBank().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true, false)
                    .when(resultSet)
                    .next();
            getMockedBankFromResultSet(expected);

            List<Bank> actual = bankDAO.findAll();

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void testShouldReturnEmptyList() {
            String sql = "SELECT * FROM banks";

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(false)
                    .when(resultSet)
                    .next();

            List<Bank> actual = bankDAO.findAll();

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class SaveTest {

        @Test
        @SneakyThrows
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = """
                    INSERT INTO banks (name, address, phone_number)
                    VALUES (?, ?, ?)
                    """;
            Bank bank = BankTestBuilder.aBank().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> bankDAO.save(bank));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            String sql = """
                    INSERT INTO banks (name, address, phone_number)
                    VALUES (?, ?, ?)
                    """;
            Bank bank = BankTestBuilder.aBank().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setMockedBankInStatement(bank);
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(bank.getId())
                    .when(resultSet)
                    .getLong(1);

            Bank actual = bankDAO.save(bank);

            assertThat(actual).isEqualTo(bank);
        }

    }

    @Nested
    class UpdateTest {

        @Test
        @SneakyThrows
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = """
                    UPDATE banks
                    SET name = ?, address = ?, phone_number = ?
                    WHERE id = ?
                    """;
            Bank bank = BankTestBuilder.aBank().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> bankDAO.update(bank));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            String sql = """
                    UPDATE banks
                    SET name = ?, address = ?, phone_number = ?
                    WHERE id = ?
                    """;
            Bank bank = BankTestBuilder.aBank().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setMockedBankInStatement(bank);
            doNothing()
                    .when(preparedStatement)
                    .setLong(4, bank.getId());
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(bank.getId())
                    .when(resultSet)
                    .getLong(1);

            Bank actual = bankDAO.update(bank);

            assertThat(actual).isEqualTo(bank);
        }

    }

    @Nested
    class DeleteTest {

        @Test
        @SneakyThrows
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = "DELETE FROM accounts WHERE bank_id = ?";
            long id = 1L;
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> bankDAO.delete(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            String accountSql = "DELETE FROM accounts WHERE bank_id = ?";
            String sql = "DELETE FROM banks WHERE id = ?";
            Bank bank = BankTestBuilder.aBank().build();
            Optional<Bank> expected = Optional.of(bank);

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(accountSql);
            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            doNothing()
                    .when(preparedStatement)
                    .setLong(1, bank.getId());
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            getMockedBankFromResultSet(bank);

            Optional<Bank> actual = bankDAO.delete(bank.getId());

            assertThat(actual).isEqualTo(expected);
        }

    }

    private void setMockedBankInStatement(Bank bank) throws SQLException {
        doNothing()
                .when(preparedStatement)
                .setString(1, bank.getName());
        doNothing()
                .when(preparedStatement)
                .setString(2, bank.getAddress());
        doNothing()
                .when(preparedStatement)
                .setString(3, bank.getPhoneNumber());
    }

    private void getMockedBankFromResultSet(Bank bank) throws SQLException {
        doReturn(bank.getId())
                .when(resultSet)
                .getLong("id");
        doReturn(bank.getName())
                .when(resultSet)
                .getString("name");
        doReturn(bank.getAddress())
                .when(resultSet)
                .getString("address");
        doReturn(bank.getPhoneNumber())
                .when(resultSet)
                .getString("phone_number");
    }

}
