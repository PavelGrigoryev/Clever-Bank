package ru.clevertec.cleverbank.filter;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.account.AccountRequestTestBuilder;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountValidationFilterTest {

    @InjectMocks
    private AccountValidationFilter accountValidationFilter;
    @Spy
    private Gson gson;
    @Mock(extraInterfaces = HttpServletRequest.class)
    private ServletRequest request;
    @Mock
    private ServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private BufferedReader bufferedReader;
    private HttpServletRequest req;

    @BeforeEach
    void setUp() {
        req = (HttpServletRequest) mockingDetails(request).getMock();
    }

    @Test
    @SneakyThrows
    @DisplayName("verify chain should doFilter without validation exceptions")
    void testChainShouldDoFilter() {
        AccountRequest accountRequest = AccountRequestTestBuilder.aAccountRequest().build();
        String json = gson.toJson(accountRequest);

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();
        doNothing()
                .when(chain)
                .doFilter(request, response);

        accountValidationFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should capture expected values")
    void testDoFilterShouldCaptureExpectedValues() {
        AccountRequest expectedAccount = AccountRequestTestBuilder.aAccountRequest().build();
        String json = gson.toJson(expectedAccount);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AccountRequest> accountCaptor = ArgumentCaptor.forClass(AccountRequest.class);
        String expectedString = "accountRequest";

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        accountValidationFilter.doFilter(request, response, chain);

        verify(req).setAttribute(stringCaptor.capture(), accountCaptor.capture());

        String actualString = stringCaptor.getValue();
        AccountRequest actualAccount = accountCaptor.getValue();

        assertAll(
                () -> assertThat(actualString).isEqualTo(expectedString),
                () -> assertThat(actualAccount).isEqualTo(expectedAccount)
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should throw ValidationException with Account can not be null message")
    void testDoFilterShouldThrowValidationExceptionWithAccountCanNotBeNullMessage() {
        String expectedMessage = """
                {"violations":[{"fieldName":"Account","exception":"Account can not be null"}]}""";

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn("", (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class,
                () -> accountValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should throw ValidationException with field can not be null message")
    void testDoFilterShouldThrowValidationExceptionWithFieldCanNotBeNullMessage() {
        AccountRequest accountRequest = AccountRequestTestBuilder.aAccountRequest()
                .withBalance(null)
                .build();
        String json = gson.toJson(accountRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"balance","exception":"Field can not be null"}]}""";

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class,
                () -> accountValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getAccountRequestArguments")
    @DisplayName("test doFilter should throw ValidationException with list of violations")
    void testDoFilterShouldThrowValidationExceptionWithListFoViolations(AccountRequest accountRequest) {
        String json = gson.toJson(accountRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"currency","exception":"Available currencies are: BYN, RUB, USD or EUR"},\
                {"fieldName":"balance","exception":"Field must be grater than 0"},\
                {"fieldName":"bank_id","exception":"Field must be grater than 0"},\
                {"fieldName":"user_id","exception":"Field must be grater than 0"}]}""";

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class,
                () -> accountValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    private static Stream<Arguments> getAccountRequestArguments() {
        return Stream.of(Arguments.of(AccountRequestTestBuilder.aAccountRequest()
                        .withCurrency(null)
                        .withBalance(BigDecimal.valueOf(-5))
                        .withBankId(-2L)
                        .withUserId(-100L)
                        .build()),
                Arguments.of(AccountRequestTestBuilder.aAccountRequest()
                        .withCurrency(null)
                        .withBalance(BigDecimal.ZERO)
                        .withBankId(0L)
                        .withUserId(0L)
                        .build()));
    }

}
