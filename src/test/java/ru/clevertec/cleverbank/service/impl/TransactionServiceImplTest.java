package ru.clevertec.cleverbank.service.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.dto.transaction.AmountStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatement;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.exception.internalservererror.TransactionException;
import ru.clevertec.cleverbank.exception.notfound.AccountNotFoundException;
import ru.clevertec.cleverbank.exception.notfound.TransactionNotFoundException;
import ru.clevertec.cleverbank.mapper.TransactionMapper;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.CheckService;
import ru.clevertec.cleverbank.service.UploadFileService;
import ru.clevertec.cleverbank.service.ValidationService;
import ru.clevertec.cleverbank.util.account.AccountTestBuilder;
import ru.clevertec.cleverbank.util.transaction.AmountStatementResponseTestBuilder;
import ru.clevertec.cleverbank.util.transaction.ChangeBalanceRequestTestBuilder;
import ru.clevertec.cleverbank.util.transaction.ChangeBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransactionResponseTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransactionStatementRequestTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransactionStatementResponseTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransactionStatementTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransactionTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransferBalanceRequestTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransferBalanceResponseTestBuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;
    @Mock
    private AccountService accountService;
    @Mock
    private TransactionDAO transactionDAO;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private CheckService checkService;
    @Mock
    private UploadFileService uploadFileService;
    @Mock
    private ValidationService validationService;
    @Mock
    private Connection connection;

    @Nested
    class ChangeBalanceTest {

        @Test
        void testShouldReturnExpectedResponseWithAddingBalance() {
            ChangeBalanceResponse expected = ChangeBalanceResponseTestBuilder.aChangeBalanceResponse().build();
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest().build();
            Account accountRecipient = AccountTestBuilder.aAccount().build();
            Account accountSender = AccountTestBuilder.aAccount().withBalance(BigDecimal.TEN).build();
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String check = "Check";
            BigDecimal newBalance = accountRecipient.getBalance().add(request.sum());

            doReturn(accountRecipient)
                    .when(accountService)
                    .findById(request.accountRecipientId());
            doReturn(accountSender)
                    .when(accountService)
                    .findById(request.accountSenderId());
            doNothing()
                    .when(validationService)
                    .validateAccountForClosingDate(accountRecipient.getClosingDate(), accountRecipient.getId());
            doNothing()
                    .when(validationService)
                    .validateAccountForSufficientBalance(request.type(), request.sum(), accountRecipient.getBalance());
            doReturn(accountRecipient)
                    .when(accountService)
                    .updateBalance(accountRecipient, newBalance);
            doReturn(transaction)
                    .when(transactionMapper)
                    .toChangeTransaction(request.type(), accountRecipient.getBank().getId(), accountSender.getBank().getId(), request);
            doReturn(transaction)
                    .when(transactionDAO)
                    .save(transaction);
            doReturn(expected)
                    .when(transactionMapper)
                    .toChangeResponse(transaction, accountSender.getBank().getName(), accountRecipient.getBank().getName(),
                            accountRecipient.getCurrency(), accountRecipient.getBalance(), newBalance);
            doReturn(check)
                    .when(checkService)
                    .createChangeBalanceCheck(expected);
            doNothing()
                    .when(uploadFileService)
                    .uploadCheck(check);

            ChangeBalanceResponse actual = transactionService.changeBalance(request);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void testShouldReturnExpectedResponseWithSubtractingBalance() {
            ChangeBalanceResponse expected = ChangeBalanceResponseTestBuilder.aChangeBalanceResponse().build();
            ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                    .withType(Type.WITHDRAWAL)
                    .build();
            Account accountRecipient = AccountTestBuilder.aAccount().build();
            Account accountSender = AccountTestBuilder.aAccount().withBalance(BigDecimal.TEN).build();
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String check = "Check";
            BigDecimal newBalance = accountRecipient.getBalance().subtract(request.sum());

            doReturn(accountRecipient)
                    .when(accountService)
                    .findById(request.accountRecipientId());
            doReturn(accountSender)
                    .when(accountService)
                    .findById(request.accountSenderId());
            doNothing()
                    .when(validationService)
                    .validateAccountForClosingDate(accountRecipient.getClosingDate(), accountRecipient.getId());
            doNothing()
                    .when(validationService)
                    .validateAccountForSufficientBalance(request.type(), request.sum(), accountRecipient.getBalance());
            doReturn(accountRecipient)
                    .when(accountService)
                    .updateBalance(accountRecipient, newBalance);
            doReturn(transaction)
                    .when(transactionMapper)
                    .toChangeTransaction(request.type(), accountRecipient.getBank().getId(), accountSender.getBank().getId(), request);
            doReturn(transaction)
                    .when(transactionDAO)
                    .save(transaction);
            doReturn(expected)
                    .when(transactionMapper)
                    .toChangeResponse(transaction, accountSender.getBank().getName(), accountRecipient.getBank().getName(),
                            accountRecipient.getCurrency(), accountRecipient.getBalance(), newBalance);
            doReturn(check)
                    .when(checkService)
                    .createChangeBalanceCheck(expected);
            doNothing()
                    .when(uploadFileService)
                    .uploadCheck(check);

            ChangeBalanceResponse actual = transactionService.changeBalance(request);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class TransferBalanceTest {

        @Test
        @SneakyThrows
        void testShouldReturnExpectedResponse() {
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest().build();
            TransferBalanceResponse expected = TransferBalanceResponseTestBuilder.aTransferBalanceResponse().build();
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            Account accountRecipient = AccountTestBuilder.aAccount().build();
            Account accountSender = AccountTestBuilder.aAccount().withBalance(BigDecimal.TEN).build();
            BigDecimal senderNewBalance = accountSender.getBalance().subtract(request.sum());
            BigDecimal recipientNewBalance = accountRecipient.getBalance().add(request.sum());
            String check = "Check";

            doNothing()
                    .when(connection)
                    .setAutoCommit(false);
            doReturn(accountSender)
                    .when(accountService)
                    .findById(request.accountSenderId());
            doReturn(accountRecipient)
                    .when(accountService)
                    .findById(request.accountRecipientId());
            doNothing()
                    .when(validationService)
                    .validateAccountForClosingDate(accountRecipient.getClosingDate(), accountRecipient.getId());
            doNothing()
                    .when(validationService)
                    .validateAccountForCurrency(accountSender.getCurrency(), accountRecipient.getCurrency());
            doNothing()
                    .when(validationService)
                    .validateAccountForSufficientBalance(Type.TRANSFER, request.sum(), accountSender.getBalance());
            doReturn(accountSender)
                    .when(accountService)
                    .updateBalance(accountSender, senderNewBalance);
            doReturn(accountRecipient)
                    .when(accountService)
                    .updateBalance(accountRecipient, recipientNewBalance);
            doReturn(transaction)
                    .when(transactionMapper)
                    .toTransferTransaction(Type.TRANSFER, accountSender.getBank().getId(), accountRecipient.getBank().getId(),
                            accountSender.getId(), accountRecipient.getId(), request.sum());
            doReturn(transaction)
                    .when(transactionDAO)
                    .save(transaction);
            doNothing()
                    .when(connection)
                    .commit();
            doReturn(expected)
                    .when(transactionMapper)
                    .toTransferResponse(transaction, accountSender.getCurrency(), accountSender.getBank().getName(),
                            accountRecipient.getBank().getName(), accountSender.getBalance(), senderNewBalance,
                            accountRecipient.getBalance(), recipientNewBalance);
            doReturn(check)
                    .when(checkService)
                    .createTransferBalanceCheck(expected);
            doNothing()
                    .when(uploadFileService)
                    .uploadCheck(check);
            doNothing()
                    .when(connection)
                    .setAutoCommit(true);

            TransferBalanceResponse actual = transactionService.transferBalance(request);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void testShouldThrowTransactionExceptionWithExpectedMessage() {
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
            String message = "Account with ID " + id + " is not found!";
            String expectedMessage = "Transaction rollback, cause: " + message;
            TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                    .withAccountSenderId(id)
                    .build();

            doNothing()
                    .when(connection)
                    .setAutoCommit(false);
            doThrow(new AccountNotFoundException(message))
                    .when(accountService)
                    .findById(id);
            doNothing()
                    .when(connection)
                    .rollback();
            doNothing()
                    .when(connection)
                    .setAutoCommit(true);

            Exception exception = assertThrows(TransactionException.class, () -> transactionService.transferBalance(request));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    @Nested
    class FindAllByPeriodOfDateAndAccountIdTest {

        @Test
        void testShouldReturnExpectedResponseThatContainsListOfSizeOne() {
            Account account = AccountTestBuilder.aAccount().build();
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            TransactionStatement statement = TransactionStatementTestBuilder.aTransactionStatement().build();
            TransactionStatementResponse expected = TransactionStatementResponseTestBuilder.aTransactionStatementResponse().build();
            String check = "Check";

            doReturn(account)
                    .when(accountService)
                    .findById(request.accountId());
            doReturn(List.of(statement))
                    .when(transactionDAO)
                    .findAllByPeriodOfDateAndAccountId(request.from(), request.to(), request.accountId());
            doReturn(expected)
                    .when(transactionMapper)
                    .toStatementResponse(account.getBank().getName(), account.getUser(), account, request, List.of(statement));
            doReturn(check)
                    .when(checkService)
                    .createTransactionStatement(expected);
            doNothing()
                    .when(uploadFileService)
                    .uploadStatement(check);

            TransactionStatementResponse actual = transactionService.findAllByPeriodOfDateAndAccountId(request);

            assertThat(actual.transactions()).hasSize(expected.transactions().size());
        }

        @Test
        void testShouldReturnExpectedResponse() {
            Account account = AccountTestBuilder.aAccount().build();
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            TransactionStatement statement = TransactionStatementTestBuilder.aTransactionStatement().build();
            TransactionStatementResponse expected = TransactionStatementResponseTestBuilder.aTransactionStatementResponse().build();
            String check = "Check";

            doReturn(account)
                    .when(accountService)
                    .findById(request.accountId());
            doReturn(List.of(statement))
                    .when(transactionDAO)
                    .findAllByPeriodOfDateAndAccountId(request.from(), request.to(), request.accountId());
            doReturn(expected)
                    .when(transactionMapper)
                    .toStatementResponse(account.getBank().getName(), account.getUser(), account, request, List.of(statement));
            doReturn(check)
                    .when(checkService)
                    .createTransactionStatement(expected);
            doNothing()
                    .when(uploadFileService)
                    .uploadStatement(check);

            TransactionStatementResponse actual = transactionService.findAllByPeriodOfDateAndAccountId(request);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void testShouldThrowTransactionNotFoundExceptionWithExpectedMessage() {
            Account account = AccountTestBuilder.aAccount().build();
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            String expectedMessage = "It is not possible to create a transaction amount because" +
                                     " you do not have any transactions for this period of time : from "
                                     + request.from() + " to " + request.to();

            doReturn(account)
                    .when(accountService)
                    .findById(request.accountId());
            doReturn(List.of())
                    .when(transactionDAO)
                    .findAllByPeriodOfDateAndAccountId(request.from(), request.to(), request.accountId());

            Exception exception = assertThrows(TransactionNotFoundException.class,
                    () -> transactionService.findAllByPeriodOfDateAndAccountId(request));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    @Nested
    class FindSumOfFundsByPeriodOfDateAndAccountIdTest {

        @Test
        void testShouldReturnExpectedResponse() {
            Account account = AccountTestBuilder.aAccount().build();
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            AmountStatementResponse expected = AmountStatementResponseTestBuilder.aAmountStatementResponse().build();
            BigDecimal spentFunds = BigDecimal.TEN;
            BigDecimal receivedFunds = BigDecimal.ONE;
            String check = "Check";

            doReturn(account)
                    .when(accountService)
                    .findById(request.accountId());
            doReturn(spentFunds)
                    .when(transactionDAO)
                    .findSumOfSpentFundsByPeriodOfDateAndAccountId(request.from(), request.to(), request.accountId());
            doReturn(receivedFunds)
                    .when(transactionDAO)
                    .findSumOfReceivedFundsByPeriodOfDateAndAccountId(request.from(), request.to(), request.accountId());
            doReturn(expected)
                    .when(transactionMapper)
                    .toAmountResponse(account.getBank().getName(), account.getUser(), account, request, spentFunds, receivedFunds);
            doReturn(check)
                    .when(checkService)
                    .createAmountStatement(expected);
            doNothing()
                    .when(uploadFileService)
                    .uploadAmount(check);

            AmountStatementResponse actual = transactionService.findSumOfFundsByPeriodOfDateAndAccountId(request);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void testShouldThrowTransactionNotFoundExceptionWithExpectedMessage() {
            Account account = AccountTestBuilder.aAccount().build();
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            String expectedMessage = "It is not possible to create a transaction amount because" +
                                     " you do not have any transactions for this period of time : from "
                                     + request.from() + " to " + request.to();

            doReturn(account)
                    .when(accountService)
                    .findById(request.accountId());
            doReturn(null)
                    .when(transactionDAO)
                    .findSumOfSpentFundsByPeriodOfDateAndAccountId(request.from(), request.to(), request.accountId());
            doReturn(null)
                    .when(transactionDAO)
                    .findSumOfReceivedFundsByPeriodOfDateAndAccountId(request.from(), request.to(), request.accountId());

            Exception exception = assertThrows(TransactionNotFoundException.class,
                    () -> transactionService.findSumOfFundsByPeriodOfDateAndAccountId(request));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    @Nested
    class FindByIdTest {

        @Test
        void testShouldThrowTransactionNotFoundExceptionWithExpectedMessage() {
            long id = 1L;
            String expectedMessage = "Transaction with ID " + id + " is not found!";

            Exception exception = assertThrows(TransactionNotFoundException.class, () -> transactionService.findById(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        void testShouldReturnExpectedResponse() {
            TransactionResponse expected = TransactionResponseTestBuilder.aTransactionResponse().build();
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            long id = expected.id();

            doReturn(expected)
                    .when(transactionMapper)
                    .toResponse(transaction);
            doReturn(Optional.of(transaction))
                    .when(transactionDAO)
                    .findById(id);

            TransactionResponse actual = transactionService.findById(id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindAllBySendersAccountIdTest {

        @Test
        void testShouldReturnListOfSizeOne() {
            TransactionResponse response = TransactionResponseTestBuilder.aTransactionResponse().build();
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
            int expectedSize = 1;

            doReturn(List.of(response))
                    .when(transactionMapper)
                    .toResponseList(List.of(transaction));
            doReturn(List.of(transaction))
                    .when(transactionDAO)
                    .findAllBySendersAccountId(id);

            List<TransactionResponse> actual = transactionService.findAllBySendersAccountId(id);

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        void testShouldReturnListThatContainsExpectedResponse() {
            TransactionResponse expected = TransactionResponseTestBuilder.aTransactionResponse().build();
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";

            doReturn(List.of(expected))
                    .when(transactionMapper)
                    .toResponseList(List.of(transaction));
            doReturn(List.of(transaction))
                    .when(transactionDAO)
                    .findAllBySendersAccountId(id);

            List<TransactionResponse> actual = transactionService.findAllBySendersAccountId(id);

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        void testShouldReturnEmptyList() {
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";

            doReturn(List.of())
                    .when(transactionDAO)
                    .findAllBySendersAccountId(id);

            List<TransactionResponse> actual = transactionService.findAllBySendersAccountId(id);

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class FindAllByRecipientAccountIdTest {

        @Test
        void testShouldReturnListOfSizeOne() {
            TransactionResponse response = TransactionResponseTestBuilder.aTransactionResponse().build();
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
            int expectedSize = 1;

            doReturn(List.of(response))
                    .when(transactionMapper)
                    .toResponseList(List.of(transaction));
            doReturn(List.of(transaction))
                    .when(transactionDAO)
                    .findAllByRecipientAccountId(id);

            List<TransactionResponse> actual = transactionService.findAllByRecipientAccountId(id);

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        void testShouldReturnListThatContainsExpectedResponse() {
            TransactionResponse expected = TransactionResponseTestBuilder.aTransactionResponse().build();
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";

            doReturn(List.of(expected))
                    .when(transactionMapper)
                    .toResponseList(List.of(transaction));
            doReturn(List.of(transaction))
                    .when(transactionDAO)
                    .findAllByRecipientAccountId(id);

            List<TransactionResponse> actual = transactionService.findAllByRecipientAccountId(id);

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        void testShouldReturnEmptyList() {
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";

            doReturn(List.of())
                    .when(transactionDAO)
                    .findAllByRecipientAccountId(id);

            List<TransactionResponse> actual = transactionService.findAllByRecipientAccountId(id);

            assertThat(actual).isEmpty();
        }

    }

}
