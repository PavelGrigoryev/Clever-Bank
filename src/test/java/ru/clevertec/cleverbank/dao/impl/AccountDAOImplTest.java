package ru.clevertec.cleverbank.dao.impl;

import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.AccountData;
import ru.clevertec.cleverbank.tables.pojos.Account;
import ru.clevertec.cleverbank.util.account.AccountDataTestBuilder;
import ru.clevertec.cleverbank.util.account.AccountTestBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static ru.clevertec.cleverbank.Tables.ACCOUNT;

@ExtendWith(MockitoExtension.class)
class AccountDAOImplTest {

    @InjectMocks
    private AccountDAOImpl accountDAO;
    @Mock
    private DSLContext dslContext;

    @Nested
    class FindByIdTest {

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            AccountData account = AccountDataTestBuilder.aAccountData().build();
            Optional<AccountData> expected = Optional.of(account);
            String id = account.getId();

            doReturn(expected)
                    .when(dslContext)
                    .select();

            Optional<AccountData> actual = accountDAO.findById(id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindAllTest {

        @Test
        @SneakyThrows
        void testShouldReturnListOfDatasSizeOne() {
            AccountData account = AccountDataTestBuilder.aAccountData().build();
            int expectedSize = 1;

            doReturn(List.of(account))
                    .when(dslContext)
                    .select();

            List<AccountData> actual = accountDAO.findAllDatas();

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        void testShouldReturnListOfAccountsSizeOne() {
            Account account = AccountTestBuilder.aAccount().build();
            int expectedSize = 1;

            doReturn(List.of(account))
                    .when(dslContext)
                    .selectFrom(ACCOUNT);

            List<Account> actual = accountDAO.findAll();

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        void testShouldReturnListThatContainsExpectedAccount() {
            Account expected = AccountTestBuilder.aAccount().build();

            doReturn(List.of(expected))
                    .when(dslContext)
                    .selectFrom(ACCOUNT);

            List<Account> actual = accountDAO.findAll();

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void testShouldReturnListThatContainsExpectedData() {
            AccountData expected = AccountDataTestBuilder.aAccountData().build();

            doReturn(List.of(expected))
                    .when(dslContext)
                    .select();

            List<AccountData> actual = accountDAO.findAllDatas();

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void testShouldReturnEmptyList() {
            doReturn(List.of())
                    .when(dslContext)
                    .selectFrom(ACCOUNT);

            List<Account> actual = accountDAO.findAll();

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class SaveTest {

        @Test
        @SneakyThrows
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            Account account = AccountTestBuilder.aAccount().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(dslContext)
                    .insertInto(ACCOUNT);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> accountDAO.save(account));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            AccountData accountData = AccountDataTestBuilder.aAccountData().build();
            Account account = AccountTestBuilder.aAccount().build();

            doReturn(accountData)
                    .when(dslContext)
                    .insertInto(ACCOUNT);

            AccountData actual = accountDAO.save(account);

            assertThat(actual).isEqualTo(accountData);
        }

    }

    @Nested
    class UpdateTest {

        @Test
        @SneakyThrows
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            Account account = AccountTestBuilder.aAccount().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(dslContext)
                    .update(ACCOUNT);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> accountDAO.update(account));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            AccountData accountData = AccountDataTestBuilder.aAccountData().build();
            Account account = AccountTestBuilder.aAccount().build();

            doReturn(accountData)
                    .when(dslContext)
                    .update(ACCOUNT);

            AccountData actual = accountDAO.update(account);

            assertThat(actual).isEqualTo(accountData);
        }

    }

    @Nested
    class DeleteTest {

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            Account account = AccountTestBuilder.aAccount().build();
            Optional<Account> expected = Optional.of(account);

            doReturn(expected)
                    .when(dslContext)
                    .deleteFrom(ACCOUNT);

            Optional<Account> actual = accountDAO.delete(account.getId());

            assertThat(actual).isEqualTo(expected);
        }

    }

}
