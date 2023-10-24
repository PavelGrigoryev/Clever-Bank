package ru.clevertec.cleverbank.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.account.AccountRequestTestBuilder;
import ru.clevertec.cleverbank.builder.account.AccountResponseTestBuilder;
import ru.clevertec.cleverbank.builder.account.AccountTestBuilder;
import ru.clevertec.cleverbank.builder.bank.BankTestBuilder;
import ru.clevertec.cleverbank.builder.user.UserTestBuilder;
import ru.clevertec.cleverbank.dao.AccountDAO;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.exception.internalservererror.FailedConnectionException;
import ru.clevertec.cleverbank.exception.notfound.AccountNotFoundException;
import ru.clevertec.cleverbank.mapper.AccountMapper;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.service.BankService;
import ru.clevertec.cleverbank.service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;
    @Mock
    private AccountDAO accountDAO;
    @Mock
    private UserService userService;
    @Mock
    private BankService bankService;
    @Mock
    private AccountMapper accountMapper;
    @Captor
    private ArgumentCaptor<Account> captor;

    @Nested
    class FindByIdTest {

        @Test
        @DisplayName("test should throw AccountNotFoundException with expected message")
        void testShouldThrowAccountNotFoundExceptionWithExpectedMessage() {
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
            String expectedMessage = "Account with ID " + id + " is not found!";

            Exception exception = assertThrows(AccountNotFoundException.class, () -> accountService.findByIdResponse(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            AccountResponse expected = AccountResponseTestBuilder.aAccountResponse().build();
            Account account = AccountTestBuilder.aAccount().build();
            String id = expected.id();

            doReturn(expected)
                    .when(accountMapper)
                    .toResponse(account);
            doReturn(Optional.of(account))
                    .when(accountDAO)
                    .findById(id);

            AccountResponse actual = accountService.findByIdResponse(id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindAllTest {

        @Test
        @DisplayName("test should return list of size one")
        void testShouldReturnListOfSizeOne() {
            AccountResponse response = AccountResponseTestBuilder.aAccountResponse().build();
            Account account = AccountTestBuilder.aAccount().build();
            int expectedSize = 1;

            doReturn(List.of(response))
                    .when(accountMapper)
                    .toResponseList(List.of(account));
            doReturn(List.of(account))
                    .when(accountDAO)
                    .findAll();

            List<AccountResponse> actual = accountService.findAllResponses();

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @DisplayName("test should return list that contains expected response")
        void testShouldReturnListThatContainsExpectedResponse() {
            AccountResponse expected = AccountResponseTestBuilder.aAccountResponse().build();
            Account account = AccountTestBuilder.aAccount().build();

            doReturn(List.of(expected))
                    .when(accountMapper)
                    .toResponseList(List.of(account));
            doReturn(List.of(account))
                    .when(accountDAO)
                    .findAll();

            List<AccountResponse> actual = accountService.findAllResponses();

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            doReturn(List.of())
                    .when(accountDAO)
                    .findAll();

            List<AccountResponse> actual = accountService.findAllResponses();

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class SaveTest {

        @ParameterizedTest(name = "{arguments} test")
        @MethodSource("ru.clevertec.cleverbank.service.impl.AccountServiceImplTest#getArgumentsForSaveTest")
        @DisplayName("test should capture value and return expected response")
        void testShouldCaptureValue(Account expected) {
            User user = UserTestBuilder.aUser().build();
            Bank bank = BankTestBuilder.aBank().build();
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest().build();
            AccountResponse response = AccountResponseTestBuilder.aAccountResponse()
                    .withId(expected.getId())
                    .withBalance(expected.getBalance())
                    .build();

            doReturn(expected)
                    .when(accountMapper)
                    .fromSaveRequest(request, user, bank);
            doReturn(user)
                    .when(userService)
                    .findById(request.userId());
            doReturn(bank)
                    .when(bankService)
                    .findById(request.bankId());
            doReturn(Optional.of(expected))
                    .when(accountDAO)
                    .save(expected);
            doReturn(response)
                    .when(accountMapper)
                    .toResponse(expected);

            accountService.save(request);
            verify(accountDAO).save(captor.capture());

            Account accountCaptor = captor.getValue();
            assertThat(accountCaptor).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should throw FailedConnectionException with expected message")
        void testShouldThrowFailedConnectionExceptionWithExpectedMessage() {
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest().build();
            String expectedMessage = "Failed to save " + request;

            Exception exception = assertThrows(FailedConnectionException.class, () -> accountService.save(request));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    @Nested
    class UpdateBalanceTest {

        @Test
        @DisplayName("test should return updated response")
        void testShouldReturnUpdatedResponse() {
            BigDecimal newBalance = BigDecimal.valueOf(1);
            Account expected = AccountTestBuilder.aAccount()
                    .withBalance(newBalance)
                    .build();

            doReturn(Optional.of(expected))
                    .when(accountDAO)
                    .update(expected);

            Account actual = accountService.updateBalance(expected, newBalance);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should throw FailedConnectionException with expected message")
        void testShouldThrowFailedConnectionExceptionWithExpectedMessage() {
            Account account = AccountTestBuilder.aAccount().build();
            BigDecimal balance = BigDecimal.TEN;
            String expectedMessage = "Failed to update balance " + balance;

            Exception exception = assertThrows(FailedConnectionException.class,
                    () -> accountService.updateBalance(account, balance));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    @Nested
    class CloseAccountTest {

        @Test
        @DisplayName("test should return updated response")
        void testShouldReturnUpdatedResponse() {
            Account account = AccountTestBuilder.aAccount().build();
            AccountResponse expected = AccountResponseTestBuilder.aAccountResponse().build();

            doReturn(Optional.of(account))
                    .when(accountDAO)
                    .findById(account.getId());
            doReturn(account)
                    .when(accountMapper)
                    .fromCloseRequest(account);
            doReturn(Optional.of(account))
                    .when(accountDAO)
                    .update(account);
            doReturn(expected)
                    .when(accountMapper)
                    .toResponse(account);

            AccountResponse actual = accountService.closeAccount(account.getId());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should throw AccountNotFoundException with expected message")
        void testShouldThrowAccountNotFoundExceptionWithExpectedMessage() {
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
            String expectedMessage = "Account with ID " + id + " is not found!";

            Exception exception = assertThrows(AccountNotFoundException.class, () -> accountService.closeAccount(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @DisplayName("test should throw FailedConnectionException with expected message")
        void testShouldThrowFailedConnectionExceptionWithExpectedMessage() {
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
            String expectedMessage = "Failed to close account by id " + id;
            Account account = AccountTestBuilder.aAccount().build();

            doReturn(Optional.of(account))
                    .when(accountDAO)
                    .findById(id);

            Exception exception = assertThrows(FailedConnectionException.class, () -> accountService.closeAccount(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    @Nested
    class DeleteTest {

        @Test
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            Account account = AccountTestBuilder.aAccount().build();
            DeleteResponse expected = new DeleteResponse("Account with ID " + account.getId() + " was successfully deleted");

            doReturn(Optional.of(account))
                    .when(accountDAO)
                    .delete(account.getId());

            DeleteResponse actual = accountService.delete(account.getId());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should throw AccountNotFoundException with expected message")
        void testShouldThrowAccountNotFoundExceptionWithExpectedMessage() {
            String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
            String expectedMessage = "No Account with ID " + id + " to delete";

            Exception exception = assertThrows(AccountNotFoundException.class, () -> accountService.delete(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    private static Stream<Arguments> getArgumentsForSaveTest() {
        return Stream.of(Arguments.of(AccountTestBuilder.aAccount().build()),
                Arguments.of(AccountTestBuilder.aAccount()
                        .withId("RSK9 NQIW GVZY ODR9 0ZS3 NA6N 9HNJ")
                        .withBalance(BigDecimal.valueOf(123.23))
                        .build()),
                Arguments.of(AccountTestBuilder.aAccount()
                        .withId("SW5C MJDI ZZN0 CTUW 5MEO 8DRA GKU2")
                        .withBalance(BigDecimal.valueOf(5050.55))
                        .build()));
    }

}
