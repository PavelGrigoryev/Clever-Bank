package ru.clevertec.cleverbank.service.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.mapper.TransactionMapper;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.CheckService;
import ru.clevertec.cleverbank.service.UploadFileService;
import ru.clevertec.cleverbank.service.ValidationService;
import ru.clevertec.cleverbank.util.account.AccountTestBuilder;
import ru.clevertec.cleverbank.util.transaction.ChangeBalanceRequestTestBuilder;
import ru.clevertec.cleverbank.util.transaction.ChangeBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransactionTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransferBalanceRequestTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransferBalanceResponseTestBuilder;

import java.math.BigDecimal;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

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
            TransferBalanceResponse response = TransferBalanceResponseTestBuilder.aTransferBalanceResponse().build();
            Account accountRecipient = AccountTestBuilder.aAccount().build();
            Account accountSender = AccountTestBuilder.aAccount().withBalance(BigDecimal.TEN).build();

            doNothing()
                    .when(connection)
                    .setAutoCommit(false);
            doNothing()
                    .when(validationService)
                    .validateAccountForClosingDate(accountSender.getClosingDate(), accountSender.getId());
            doNothing()
                    .when(validationService)
                    .validateAccountForClosingDate(accountRecipient.getClosingDate(), accountRecipient.getId());
            doNothing()
                    .when(validationService)
                    .validateAccountForCurrency(accountSender.getCurrency(), accountRecipient.getCurrency());

            doNothing()
                    .when(validationService)
                    .validateAccountForSufficientBalance(Type.TRANSFER, request.sum(), accountSender.getBalance());
        }

    }

}

