package ru.clevertec.cleverbank.dao.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.bank.BankTestBuilder;
import ru.clevertec.cleverbank.model.Bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
        @DisplayName("test should throw SQLException with expected message if there is no connection")
        void testShouldThrowSQLExceptionWithExpectedMessage() {
            String sql = "SELECT * FROM banks WHERE id = ?";
            long id = 1L;
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            assertDoesNotThrow(() -> bankDAO.findById(id));

            Exception exception = assertThrows(SQLException.class, () -> connection.prepareStatement(sql));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = "SELECT * FROM banks WHERE id = ?";
            Bank expected = BankTestBuilder.aBank().build();
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
            getMockedBankFromResultSet(expected);

            bankDAO.findById(id)
                    .ifPresent(actual -> assertThat(actual).isEqualTo(expected));
        }

    }

    @Nested
    class FindAllTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw SQLException with expected message if there is no connection")
        void testShouldThrowSQLExceptionWithExpectedMessage() {
            String sql = "SELECT * FROM banks";
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            assertDoesNotThrow(() -> bankDAO.findAll());

            Exception exception = assertThrows(SQLException.class, () -> connection.prepareStatement(sql));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list of size one")
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
        @DisplayName("test should return list that contains expected response")
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
        @DisplayName("test should return empty list")
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
        @DisplayName("test should throw SQLException with expected message if there is no connection")
        void testShouldThrowSQLExceptionWithExpectedMessage() {
            String sql = """
                    INSERT INTO banks (name, address, phone_number)
                    VALUES (?, ?, ?)
                    """;
            Bank bank = BankTestBuilder.aBank().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            assertDoesNotThrow(() -> bankDAO.save(bank));

            Exception exception = assertThrows(SQLException.class,
                    () -> connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
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

            bankDAO.save(bank)
                    .ifPresent(actual -> assertThat(actual).isEqualTo(bank));
        }

    }

    @Nested
    class UpdateTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw SQLException with expected message if there is no connection")
        void testShouldThrowSQLExceptionWithExpectedMessage() {
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

            assertDoesNotThrow(() -> bankDAO.update(bank));

            Exception exception = assertThrows(SQLException.class,
                    () -> connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
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

            bankDAO.update(bank)
                    .ifPresent(actual -> assertThat(actual).isEqualTo(bank));
        }

    }

    @Nested
    class DeleteTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw SQLException with expected message if there is no connection")
        void testShouldThrowSQLExceptionWithExpectedMessage() {
            String sql = "DELETE FROM banks WHERE id = ?";
            long id = 1L;
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            assertDoesNotThrow(() -> bankDAO.delete(id));

            Exception exception = assertThrows(SQLException.class,
                    () -> connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            String accountSql = "DELETE FROM accounts WHERE bank_id = ?";
            String sql = "DELETE FROM banks WHERE id = ?";
            Bank expected = BankTestBuilder.aBank().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(accountSql);
            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            doNothing()
                    .when(preparedStatement)
                    .setLong(1, expected.getId());
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            getMockedBankFromResultSet(expected);

            bankDAO.delete(expected.getId())
                    .ifPresent(actual -> assertThat(actual).isEqualTo(expected));
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
