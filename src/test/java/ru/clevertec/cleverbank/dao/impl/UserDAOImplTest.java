package ru.clevertec.cleverbank.dao.impl;

import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.exception.badrequest.UniquePhoneNumberException;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.tables.pojos.User;
import ru.clevertec.cleverbank.util.user.UserTestBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static ru.clevertec.cleverbank.Tables.ACCOUNT;
import static ru.clevertec.cleverbank.Tables.USER;

@ExtendWith(MockitoExtension.class)
class UserDAOImplTest {

    @InjectMocks
    private UserDAOImpl userDAO;
    @Mock
    private DSLContext dslContext;

    @Nested
    class FindByIdTest {

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            User user = UserTestBuilder.aUser().build();
            Optional<User> expected = Optional.of(user);
            long id = user.getId();

            doReturn(expected)
                    .when(dslContext)
                    .fetchOptional(USER, USER.ID.eq(id));

            Optional<User> actual = userDAO.findById(id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindAllTest {

        @Test
        @SneakyThrows
        void testShouldReturnListOfSizeOne() {
            User user = UserTestBuilder.aUser().build();
            int expectedSize = 1;

            doReturn(List.of(user))
                    .when(dslContext)
                    .selectFrom(USER);

            List<User> actual = userDAO.findAll();

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        void testShouldReturnListThatContainsExpectedResponse() {
            User expected = UserTestBuilder.aUser().build();

            doReturn(List.of(expected))
                    .when(dslContext)
                    .selectFrom(USER);

            List<User> actual = userDAO.findAll();

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void testShouldReturnEmptyList() {
            doReturn(List.of())
                    .when(dslContext)
                    .selectFrom(USER);

            List<User> actual = userDAO.findAll();

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class SaveTest {

        @Test
        @SneakyThrows
        void testShouldThrowUniquePhoneNumberExceptionWithExpectedMessage() {
            User user = UserTestBuilder.aUser().build();
            String expectedMessage = "User with phone number " + user.getMobileNumber() + " is already exist";

            doThrow(new IntegrityConstraintViolationException(expectedMessage))
                    .when(dslContext)
                    .insertInto(USER);

            Exception exception = assertThrows(UniquePhoneNumberException.class, () -> userDAO.save(user));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            User user = UserTestBuilder.aUser().build();

            doReturn(user)
                    .when(dslContext)
                    .insertInto(USER);

            User actual = userDAO.save(user);

            assertThat(actual).isEqualTo(user);
        }

    }

    @Nested
    class UpdateTest {

        @Test
        @SneakyThrows
        void testShouldThrowUniquePhoneNumberExceptionWithExpectedMessage() {
            User user = UserTestBuilder.aUser().build();
            String expectedMessage = "User with phone number " + user.getMobileNumber() + " is already exist";

            doThrow(new IntegrityConstraintViolationException(expectedMessage))
                    .when(dslContext)
                    .update(USER);

            Exception exception = assertThrows(UniquePhoneNumberException.class, () -> userDAO.update(user));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            User user = UserTestBuilder.aUser().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException())
                    .when(dslContext)
                    .update(USER);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> userDAO.update(user));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            User user = UserTestBuilder.aUser().build();

            doReturn(user)
                    .when(dslContext)
                    .update(USER);

            User actual = userDAO.update(user);

            assertThat(actual).isEqualTo(user);
        }

    }

    @Nested
    class DeleteTest {

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            User user = UserTestBuilder.aUser().build();
            Optional<User> expected = Optional.of(user);

            doNothing()
                    .when(dslContext)
                    .deleteFrom(ACCOUNT);
            doReturn(expected)
                    .when(dslContext)
                    .deleteFrom(USER);

            Optional<User> actual = userDAO.delete(user.getId());

            assertThat(actual).isEqualTo(expected);
        }

    }

}
