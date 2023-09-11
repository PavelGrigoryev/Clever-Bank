package ru.clevertec.cleverbank.dao.impl;

import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatement;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.tables.pojos.Transaction;
import ru.clevertec.cleverbank.util.transaction.TransactionStatementTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransactionTestBuilder;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.impl.DSL.sum;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static ru.clevertec.cleverbank.Tables.TRANSACTION;
import static ru.clevertec.cleverbank.Tables.USER;

@ExtendWith(MockitoExtension.class)
class TransactionDAOImplTest {

    @InjectMocks
    private TransactionDAOImpl transactionDAO;
    @Mock
    private DSLContext dslContext;

    @Nested
    class FindByIdTest {

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            Optional<Transaction> expected = Optional.of(transaction);
            long id = transaction.getId();

            doReturn(expected)
                    .when(dslContext)
                    .fetchOptional(TRANSACTION, TRANSACTION.ID.eq(id));

            Optional<Transaction> actual = transactionDAO.findById(id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindAllBySendersAccountIdTest {

        @Test
        @SneakyThrows
        void testShouldReturnListOfSizeOne() {
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            int expectedSize = 1;

            doReturn(List.of(transaction))
                    .when(dslContext)
                    .selectFrom(TRANSACTION);

            List<Transaction> actual = transactionDAO.findAllBySendersAccountId(id);

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        void testShouldReturnListThatContainsExpectedResponse() {
            Transaction expected = TransactionTestBuilder.aTransaction().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";

            doReturn(List.of(expected))
                    .when(dslContext)
                    .selectFrom(TRANSACTION);

            List<Transaction> actual = transactionDAO.findAllBySendersAccountId(id);

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void testShouldReturnEmptyList() {
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";

            doReturn(List.of())
                    .when(dslContext)
                    .selectFrom(TRANSACTION);

            List<Transaction> actual = transactionDAO.findAllBySendersAccountId(id);

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class FindAllByRecipientAccountIdTest {

        @Test
        @SneakyThrows
        void testShouldReturnListOfSizeOne() {
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            int expectedSize = 1;

            doReturn(List.of(transaction))
                    .when(dslContext)
                    .selectFrom(TRANSACTION);

            List<Transaction> actual = transactionDAO.findAllByRecipientAccountId(id);

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        void testShouldReturnListThatContainsExpectedResponse() {
            Transaction expected = TransactionTestBuilder.aTransaction().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";

            doReturn(List.of(expected))
                    .when(dslContext)
                    .selectFrom(TRANSACTION);

            List<Transaction> actual = transactionDAO.findAllByRecipientAccountId(id);

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void testShouldReturnEmptyList() {
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";

            doReturn(List.of())
                    .when(dslContext)
                    .selectFrom(TRANSACTION);

            List<Transaction> actual = transactionDAO.findAllByRecipientAccountId(id);

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class SaveTest {

        @Test
        @SneakyThrows
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException())
                    .when(dslContext)
                    .insertInto(TRANSACTION);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> transactionDAO.save(transaction));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            Transaction transaction = TransactionTestBuilder.aTransaction().build();

            doReturn(transaction)
                    .when(dslContext)
                    .insertInto(TRANSACTION);

            Transaction actual = transactionDAO.save(transaction);

            assertThat(actual).isEqualTo(transaction);
        }

    }

    @Nested
    class FindAllByPeriodOfDateAndAccountIdTest {

        @Test
        @SneakyThrows
        void testShouldReturnListOfSizeOne() {
            TransactionStatement statement = TransactionStatementTestBuilder.aTransactionStatement().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);
            int expectedSize = 1;

            doReturn(List.of(statement))
                    .when(dslContext)
                    .select(TRANSACTION.DATE, TRANSACTION.TYPE, USER.LASTNAME, TRANSACTION.SUM);

            List<TransactionStatement> actual = transactionDAO.findAllByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @SneakyThrows
        void testShouldReturnListThatContainsExpectedResponse() {
            TransactionStatement expected = TransactionStatementTestBuilder.aTransactionStatement().build();
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);

            doReturn(List.of(expected))
                    .when(dslContext)
                    .select(TRANSACTION.DATE, TRANSACTION.TYPE, USER.LASTNAME, TRANSACTION.SUM);

            List<TransactionStatement> actual = transactionDAO.findAllByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void testShouldReturnEmptyList() {
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);

            doReturn(List.of())
                    .when(dslContext)
                    .select(TRANSACTION.DATE, TRANSACTION.TYPE, USER.LASTNAME, TRANSACTION.SUM);

            List<TransactionStatement> actual = transactionDAO.findAllByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class FindSumOfSpentFundsByPeriodOfDateAndAccountIdTest {

        @Test
        @SneakyThrows
        void testShouldReturnBigDecimalZero() {
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);

            doReturn(BigDecimal.ZERO)
                    .when(dslContext)
                    .select(sum(TRANSACTION.SUM).as("spent"));

            BigDecimal actual = transactionDAO.findSumOfSpentFundsByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            BigDecimal expected = BigDecimal.TEN;
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);

            doReturn(expected)
                    .when(dslContext)
                    .select(sum(TRANSACTION.SUM).as("spent"));

            BigDecimal actual = transactionDAO.findSumOfSpentFundsByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindSumOfReceivedFundsByPeriodOfDateAndAccountIdTest {

        @Test
        @SneakyThrows
        void testShouldReturnBigDecimalZero() {
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);

            doReturn(BigDecimal.ZERO)
                    .when(dslContext)
                    .select(sum(TRANSACTION.SUM).as("received"));

            BigDecimal actual = transactionDAO.findSumOfSpentFundsByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            BigDecimal expected = BigDecimal.TEN;
            String id = "OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE";
            LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
            LocalDate to = LocalDate.of(2020, Month.MAY, 12);

            doReturn(expected)
                    .when(dslContext)
                    .select(sum(TRANSACTION.SUM).as("received"));

            BigDecimal actual = transactionDAO.findSumOfReceivedFundsByPeriodOfDateAndAccountId(from, to, id);

            assertThat(actual).isEqualTo(expected);
        }

    }

}
