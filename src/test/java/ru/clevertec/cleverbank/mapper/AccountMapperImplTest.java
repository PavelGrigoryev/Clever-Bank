package ru.clevertec.cleverbank.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.account.AccountRequestTestBuilder;
import ru.clevertec.cleverbank.builder.account.AccountResponseTestBuilder;
import ru.clevertec.cleverbank.builder.account.AccountTestBuilder;
import ru.clevertec.cleverbank.builder.bank.BankTestBuilder;
import ru.clevertec.cleverbank.builder.user.UserTestBuilder;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountMapperImplTest {

    @Spy
    private AccountMapperImpl accountMapper;

    @Nested
    class ToResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            Account account = AccountTestBuilder.aAccount().build();
            AccountResponse expected = AccountResponseTestBuilder.aAccountResponse().build();

            AccountResponse actual = accountMapper.toResponse(account);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return expected value with null user and bank")
        void testShouldReturnExpectedValueWithNullUserAndBank() {
            Account account = AccountTestBuilder.aAccount()
                    .withBank(null)
                    .withUser(null)
                    .build();
            AccountResponse expected = AccountResponseTestBuilder.aAccountResponse()
                    .withBank(null)
                    .withUser(null)
                    .build();

            AccountResponse actual = accountMapper.toResponse(account);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            AccountResponse actual = accountMapper.toResponse(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToResponseListTest {

        @Test
        @DisplayName("test should return expected list")
        void testShouldReturnExpectedList() {
            List<Account> accounts = List.of(AccountTestBuilder.aAccount().build());
            List<AccountResponse> expectedList = List.of(AccountResponseTestBuilder.aAccountResponse().build());

            List<AccountResponse> actualList = accountMapper.toResponseList(accounts);

            assertThat(actualList).isEqualTo(expectedList);
        }

        @Test
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            List<AccountResponse> actual = accountMapper.toResponseList(List.of());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            List<AccountResponse> actual = accountMapper.toResponseList(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class FromSaveRequestTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            AccountRequest request = AccountRequestTestBuilder.aAccountRequest().build();
            User user = UserTestBuilder.aUser().build();
            Bank bank = BankTestBuilder.aBank().build();
            Account expected = AccountTestBuilder.aAccount()
                    .withId(null)
                    .withCurrency(request.currency())
                    .withBalance(request.balance())
                    .withOpeningDate(LocalDate.now())
                    .build();

            Account actual = accountMapper.fromSaveRequest(request, user, bank);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            Account actual = accountMapper.fromSaveRequest(null, null, null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class FromCloseRequestTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            Account account = AccountTestBuilder.aAccount().build();
            Account expected = AccountTestBuilder.aAccount()
                    .withClosingDate(LocalDate.now())
                    .withBalance(BigDecimal.ZERO)
                    .build();

            Account actual = accountMapper.fromCloseRequest(account);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            Account actual = accountMapper.fromCloseRequest(null);

            assertThat(actual).isNull();
        }

    }

}
