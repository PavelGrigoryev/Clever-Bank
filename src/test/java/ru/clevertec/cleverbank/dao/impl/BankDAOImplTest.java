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
import ru.clevertec.cleverbank.tables.pojos.Bank;
import ru.clevertec.cleverbank.util.bank.BankTestBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static ru.clevertec.cleverbank.Tables.ACCOUNT;
import static ru.clevertec.cleverbank.Tables.BANK;

@ExtendWith(MockitoExtension.class)
class BankDAOImplTest {

    @InjectMocks
    private BankDAOImpl bankDAO;
    @Mock
    private DSLContext dslContext;

    @Nested
    class FindByIdTest {

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            Bank bank = BankTestBuilder.aBank().build();
            Optional<Bank> expected = Optional.of(bank);
            long id = bank.getId();

            doReturn(expected)
                    .when(dslContext)
                    .fetchOptional(BANK, BANK.ID.eq(id));

            Optional<Bank> actual = bankDAO.findById(id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindAllTest {

        @Test
        @SneakyThrows
        void testShouldReturnListOfSizeOne() {
            Bank bank = BankTestBuilder.aBank().build();
            int expectedSize = 1;

            doReturn(List.of(bank))
                    .when(dslContext)
                    .selectFrom(BANK);

            List<Bank> actual = bankDAO.findAll();

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        void testShouldReturnListThatContainsExpectedResponse() {
            Bank expected = BankTestBuilder.aBank().build();

            doReturn(expected)
                    .when(dslContext)
                    .selectFrom(BANK);

            List<Bank> actual = bankDAO.findAll();

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void testShouldReturnEmptyList() {
            doReturn(List.of())
                    .when(dslContext)
                    .selectFrom(BANK);

            List<Bank> actual = bankDAO.findAll();

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class SaveTest {

        @Test
        @SneakyThrows
        void testShouldThrowUniquePhoneNumberExceptionWithExpectedMessage() {
            Bank bank = BankTestBuilder.aBank().build();
            String expectedMessage = "Bank with phone number " + bank.getPhoneNumber() + " is already exist";

            doThrow(new SQLException())
                    .when(dslContext)
                    .insertInto(BANK);

            Exception exception = assertThrows(UniquePhoneNumberException.class, () -> bankDAO.save(bank));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            Bank bank = BankTestBuilder.aBank().build();

            doReturn(bank)
                    .when(dslContext)
                    .insertInto(BANK);

            Bank actual = bankDAO.save(bank);

            assertThat(actual).isEqualTo(bank);
        }

    }

    @Nested
    class UpdateTest {

        @Test
        @SneakyThrows
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            Bank bank = BankTestBuilder.aBank().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException())
                    .when(dslContext)
                    .update(BANK);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> bankDAO.update(bank));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldThrowUniquePhoneNumberExceptionWithExpectedMessage() {
            Bank bank = BankTestBuilder.aBank().build();
            String expectedMessage = "Bank with phone number " + bank.getPhoneNumber() + " is already exist";

            doThrow(new IntegrityConstraintViolationException(expectedMessage))
                    .when(dslContext)
                    .update(BANK);

            Exception exception = assertThrows(UniquePhoneNumberException.class, () -> bankDAO.update(bank));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            Bank bank = BankTestBuilder.aBank().build();

            doReturn(bank)
                    .when(dslContext)
                    .update(BANK);

            Bank actual = bankDAO.update(bank);

            assertThat(actual).isEqualTo(bank);
        }

    }

    @Nested
    class DeleteTest {

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            Bank bank = BankTestBuilder.aBank().build();
            Optional<Bank> expected = Optional.of(bank);

            doNothing()
                    .when(dslContext)
                    .deleteFrom(ACCOUNT);
            doReturn(expected)
                    .when(dslContext)
                    .deleteFrom(BANK);

            Optional<Bank> actual = bankDAO.delete(bank.getId());

            assertThat(actual).isEqualTo(expected);
        }

    }

}
