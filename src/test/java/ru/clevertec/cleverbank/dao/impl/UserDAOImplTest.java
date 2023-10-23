package ru.clevertec.cleverbank.dao.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.user.UserTestBuilder;
import ru.clevertec.cleverbank.model.User;

import java.sql.Connection;
import java.sql.Date;
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
class UserDAOImplTest {

    @InjectMocks
    private UserDAOImpl userDAO;
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
            String sql = "SELECT * FROM users WHERE id = ?";
            long id = 1L;
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            assertDoesNotThrow(() -> userDAO.findById(id));

            Exception exception = assertThrows(SQLException.class, () -> connection.prepareStatement(sql));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = "SELECT * FROM users WHERE id = ?";
            User expected = UserTestBuilder.aUser().build();
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
            getMockedUserFromResultSet(expected);

            userDAO.findById(id)
                    .ifPresent(actual -> assertThat(actual).isEqualTo(expected));
        }

    }

    @Nested
    class FindAllTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw SQLException with expected message if there is no connection")
        void testShouldThrowSQLExceptionWithExpectedMessage() {
            String sql = "SELECT * FROM users";
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            assertDoesNotThrow(() -> userDAO.findAll());

            Exception exception = assertThrows(SQLException.class, () -> connection.prepareStatement(sql));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list of size one")
        void testShouldReturnListOfSizeOne() {
            String sql = "SELECT * FROM users";
            User user = UserTestBuilder.aUser().build();
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
            getMockedUserFromResultSet(user);

            List<User> actual = userDAO.findAll();

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return list that contains expected response")
        void testShouldReturnListThatContainsExpectedResponse() {
            String sql = "SELECT * FROM users";
            User expected = UserTestBuilder.aUser().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true, false)
                    .when(resultSet)
                    .next();
            getMockedUserFromResultSet(expected);

            List<User> actual = userDAO.findAll();

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            String sql = "SELECT * FROM users";

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(false)
                    .when(resultSet)
                    .next();

            List<User> actual = userDAO.findAll();

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
                    INSERT INTO users (lastname, firstname, surname, register_date, mobile_number)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            User user = UserTestBuilder.aUser().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            assertDoesNotThrow(() -> userDAO.save(user));

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
                    INSERT INTO users (lastname, firstname, surname, register_date, mobile_number)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            User user = UserTestBuilder.aUser().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setMockedUserInStatement(user);
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(user.getId())
                    .when(resultSet)
                    .getLong(1);

            userDAO.save(user)
                    .ifPresent(actual -> assertThat(actual).isEqualTo(user));
        }

    }

    @Nested
    class UpdateTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw SQLException with expected message if there is no connection")
        void testShouldThrowSQLExceptionWithExpectedMessage() {
            String sql = """
                    UPDATE users
                    SET lastname = ?, firstname = ?, surname = ?, register_date = ?, mobile_number = ?
                    WHERE id = ?
                    """;
            User user = UserTestBuilder.aUser().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            assertDoesNotThrow(() -> userDAO.update(user));

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
                    UPDATE users
                    SET lastname = ?, firstname = ?, surname = ?, register_date = ?, mobile_number = ?
                    WHERE id = ?
                    """;
            User user = UserTestBuilder.aUser().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setMockedUserInStatement(user);
            doNothing()
                    .when(preparedStatement)
                    .setLong(6, user.getId());
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(user.getId())
                    .when(resultSet)
                    .getLong(1);

            userDAO.update(user)
                    .ifPresent(actual -> assertThat(actual).isEqualTo(user));
        }

    }

    @Nested
    class DeleteTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw SQLException with expected message if there is no connection")
        void testShouldThrowSQLExceptionWithExpectedMessage() {
            String sql = "DELETE FROM users WHERE id = ?";
            long id = 1L;
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            assertDoesNotThrow(() -> userDAO.delete(id));

            Exception exception = assertThrows(SQLException.class,
                    () -> connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String accountSql = "DELETE FROM accounts WHERE user_id = ?";
            String sql = "DELETE FROM users WHERE id = ?";
            User expected = UserTestBuilder.aUser().build();

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
            getMockedUserFromResultSet(expected);

            userDAO.delete(expected.getId())
                    .ifPresent(actual -> assertThat(actual).isEqualTo(expected));
        }

    }

    private void setMockedUserInStatement(User user) throws SQLException {
        doNothing()
                .when(preparedStatement)
                .setString(1, user.getLastname());
        doNothing()
                .when(preparedStatement)
                .setString(2, user.getFirstname());
        doNothing()
                .when(preparedStatement)
                .setString(3, user.getSurname());
        doNothing()
                .when(preparedStatement)
                .setObject(4, user.getRegisterDate());
        doNothing()
                .when(preparedStatement)
                .setString(5, user.getMobileNumber());
    }

    private void getMockedUserFromResultSet(User user) throws SQLException {
        doReturn(user.getId())
                .when(resultSet)
                .getLong("id");
        doReturn(user.getLastname())
                .when(resultSet)
                .getString("lastname");
        doReturn(user.getFirstname())
                .when(resultSet)
                .getString("firstname");
        doReturn(user.getSurname())
                .when(resultSet)
                .getString("surname");
        doReturn(Date.valueOf(user.getRegisterDate()))
                .when(resultSet)
                .getDate("register_date");
        doReturn(user.getMobileNumber())
                .when(resultSet)
                .getString("mobile_number");
    }

}
