package ru.clevertec.cleverbank.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.account.AccountTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.AmountStatementResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.ChangeBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.ExchangeBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionRequestTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionStatementRequestTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionStatementResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionStatementTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransferBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.builder.user.UserTestBuilder;
import ru.clevertec.cleverbank.dto.transaction.AmountStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.ExchangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatement;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class TransactionMapperImplTest {

    @InjectMocks
    private TransactionMapperImpl transactionMapper;

    @Nested
    class ToChangeTransactionTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            TransactionRequest request = TransactionRequestTestBuilder.aTransactionRequest().build();
            Transaction expected = TransactionTestBuilder.aTransaction()
                    .withId(null)
                    .withDate(LocalDate.now())
                    .withTime(null)
                    .withAccountSenderId(request.accountSenderId())
                    .withAccountRecipientId(request.accountRecipientId())
                    .build();

            Transaction actual = transactionMapper.toChangeTransaction(Type.REPLENISHMENT, expected.getBankRecipientId(),
                    expected.getBankSenderId(), request);
            actual.setTime(null);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            Transaction actual = transactionMapper.toChangeTransaction(null, null,
                    null, null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToChangeResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            ChangeBalanceResponse expected = ChangeBalanceResponseTestBuilder.aChangeBalanceResponse()
                    .withAccountRecipientId(transaction.getAccountRecipientId())
                    .build();

            ChangeBalanceResponse actual = transactionMapper.toChangeResponse(transaction, expected.bankSenderName(),
                    expected.bankRecipientName(), expected.currency(), expected.oldBalance(), expected.newBalance());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            ChangeBalanceResponse actual = transactionMapper.toChangeResponse(null, null,
                    null, null, null, null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToTransferTransactionTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            TransactionRequest request = TransactionRequestTestBuilder.aTransactionRequest().build();
            Transaction expected = TransactionTestBuilder.aTransaction()
                    .withType(Type.TRANSFER)
                    .withId(null)
                    .withDate(LocalDate.now())
                    .withTime(null)
                    .withAccountSenderId(request.accountSenderId())
                    .withAccountRecipientId(request.accountRecipientId())
                    .build();

            Transaction actual = transactionMapper.toTransferTransaction(Type.TRANSFER, expected.getBankSenderId(),
                    expected.getBankRecipientId(), expected.getAccountSenderId(), expected.getAccountRecipientId(),
                    expected.getSumSender());
            actual.setTime(null);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            Transaction actual = transactionMapper.toTransferTransaction(null, null, null,
                    null, null, null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToTransferResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            Transaction transaction = TransactionTestBuilder.aTransaction()
                    .withType(Type.TRANSFER)
                    .build();
            TransferBalanceResponse expected = TransferBalanceResponseTestBuilder.aTransferBalanceResponse()
                    .withAccountRecipientId(transaction.getAccountRecipientId())
                    .withAccountSenderId(transaction.getAccountSenderId())
                    .build();

            TransferBalanceResponse actual = transactionMapper.toTransferResponse(transaction, expected.currency(),
                    expected.bankSenderName(), expected.bankRecipientName(), expected.senderOldBalance(),
                    expected.senderNewBalance(), expected.recipientOldBalance(), expected.recipientNewBalance());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            TransferBalanceResponse actual = transactionMapper.toTransferResponse(null, null,
                    null, null, null, null,
                    null, null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToExchangeTransactionTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            TransactionRequest request = TransactionRequestTestBuilder.aTransactionRequest().build();
            Transaction expected = TransactionTestBuilder.aTransaction()
                    .withType(Type.EXCHANGE)
                    .withId(null)
                    .withDate(LocalDate.now())
                    .withTime(null)
                    .withAccountSenderId(request.accountSenderId())
                    .withAccountRecipientId(request.accountRecipientId())
                    .build();

            Transaction actual = transactionMapper.toExchangeTransaction(Type.EXCHANGE, expected.getBankSenderId(),
                    expected.getBankRecipientId(), expected.getAccountSenderId(), expected.getAccountRecipientId(),
                    expected.getSumSender(), expected.getSumRecipient());
            actual.setTime(null);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            Transaction actual = transactionMapper.toExchangeTransaction(null, null, null,
                    null, null, null, null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToExchangeResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            Transaction transaction = TransactionTestBuilder.aTransaction()
                    .withType(Type.EXCHANGE)
                    .build();
            ExchangeBalanceResponse expected = ExchangeBalanceResponseTestBuilder.aExchangeBalanceResponse()
                    .withAccountRecipientId(transaction.getAccountRecipientId())
                    .withAccountSenderId(transaction.getAccountSenderId())
                    .withSumSender(transaction.getSumSender())
                    .withSumRecipient(transaction.getSumRecipient())
                    .build();

            ExchangeBalanceResponse actual = transactionMapper.toExchangeResponse(transaction, expected.currencySender(),
                    expected.currencyRecipient(), expected.bankSenderName(), expected.bankRecipientName(),
                    expected.senderOldBalance(), expected.senderNewBalance(), expected.recipientOldBalance(),
                    expected.recipientNewBalance());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            ExchangeBalanceResponse actual = transactionMapper.toExchangeResponse(null, null,
                    null, null, null, null,
                    null, null, null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            Transaction transaction = TransactionTestBuilder.aTransaction().build();
            TransactionResponse expected = TransactionResponseTestBuilder.aTransactionResponse().build();

            TransactionResponse actual = transactionMapper.toResponse(transaction);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            TransactionResponse actual = transactionMapper.toResponse(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToResponseListTest {

        @Test
        @DisplayName("test should return expected list")
        void testShouldReturnExpectedList() {
            List<Transaction> transactions = List.of(TransactionTestBuilder.aTransaction().build());
            List<TransactionResponse> expectedList = List.of(TransactionResponseTestBuilder.aTransactionResponse().build());

            List<TransactionResponse> actualList = transactionMapper.toResponseList(transactions);

            assertThat(actualList).isEqualTo(expectedList);
        }

        @Test
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            List<TransactionResponse> actual = transactionMapper.toResponseList(List.of());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            List<TransactionResponse> actual = transactionMapper.toResponseList(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToStatementResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            TransactionStatement transactionStatement = TransactionStatementTestBuilder.aTransactionStatement().build();
            User user = UserTestBuilder.aUser().build();
            Account account = AccountTestBuilder.aAccount().build();
            TransactionStatementResponse expected = TransactionStatementResponseTestBuilder
                    .aTransactionStatementResponse()
                    .withBalance(account.getBalance())
                    .build();

            TransactionStatementResponse actual = transactionMapper.toStatementResponse(expected.bankName(), user,
                    account, request, List.of(transactionStatement));

            assertAll(
                    () -> assertThat(actual.transactions()).isEqualTo(expected.transactions()),
                    () -> assertThat(actual.balance()).isEqualTo(expected.balance()),
                    () -> assertThat(actual.firstname()).isEqualTo(expected.firstname())
            );

        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            TransactionStatementResponse actual = transactionMapper.toStatementResponse(null, null,
                    null, null, null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToAmountResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            User user = UserTestBuilder.aUser().build();
            Account account = AccountTestBuilder.aAccount().build();
            AmountStatementResponse expected = AmountStatementResponseTestBuilder.aAmountStatementResponse().build();

            AmountStatementResponse actual = transactionMapper.toAmountResponse(expected.bankName(), user,
                    account, request, expected.spentFunds(), expected.receivedFunds());

            assertAll(
                    () -> assertThat(actual.spentFunds()).isEqualTo(expected.spentFunds()),
                    () -> assertThat(actual.receivedFunds()).isEqualTo(expected.receivedFunds()),
                    () -> assertThat(actual.firstname()).isEqualTo(expected.firstname())
            );

        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            AmountStatementResponse actual = transactionMapper.toAmountResponse(null, null,
                    null, null, null, null);

            assertThat(actual).isNull();
        }

    }

}
