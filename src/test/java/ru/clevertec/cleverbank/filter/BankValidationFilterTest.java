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
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.bank.BankRequestTestBuilder;
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;

import java.io.BufferedReader;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankValidationFilterTest {

    @InjectMocks
    private BankValidationFilter bankValidationFilter;
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
        BankRequest bankRequest = BankRequestTestBuilder.aBankRequest().build();
        String json = gson.toJson(bankRequest);

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

        bankValidationFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should capture expected values")
    void testDoFilterShouldCaptureExpectedValues() {
        BankRequest expectedBank = BankRequestTestBuilder.aBankRequest().build();
        String json = gson.toJson(expectedBank);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BankRequest> bankCaptor = ArgumentCaptor.forClass(BankRequest.class);
        String expectedString = "bankRequest";

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        bankValidationFilter.doFilter(request, response, chain);

        verify(req).setAttribute(stringCaptor.capture(), bankCaptor.capture());

        String actualString = stringCaptor.getValue();
        BankRequest actualBank = bankCaptor.getValue();

        assertAll(
                () -> assertThat(actualString).isEqualTo(expectedString),
                () -> assertThat(actualBank).isEqualTo(expectedBank)
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should throw ValidationException with Bank can not be null message")
    void testDoFilterShouldThrowValidationExceptionWithBankCanNotBeNullMessage() {
        String expectedMessage = """
                {"violations":[{"fieldName":"Bank","exception":"Bank can not be null"}]}""";

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
                () -> bankValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should throw ValidationException with field can not be null message")
    void testDoFilterShouldThrowValidationExceptionWithFieldCanNotBeNullMessage() {
        BankRequest bankRequest = BankRequestTestBuilder.aBankRequest()
                .withName(null)
                .build();
        String json = gson.toJson(bankRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"name","exception":"Field can not be null"}]}""";

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
                () -> bankValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should throw ValidationException with list of violations")
    void testDoFilterShouldThrowValidationExceptionWithListFoViolations() {
        BankRequest bankRequest = BankRequestTestBuilder.aBankRequest()
                .withName("Best Bank 99")
                .withAddress("street This!")
                .withPhoneNumber("007")
                .build();
        String json = gson.toJson(bankRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"name","exception":"Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ @_-]+$"},\
                {"fieldName":"address","exception":"Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ0-9 .,-]+$"},\
                {"fieldName":"phone_number","exception":"Field is out of pattern: \
                ^\\\\+\\\\d{1,3} \\\\(\\\\d{1,3}\\\\) \\\\d{3}-\\\\d{2}-\\\\d{2}$"}]}""";

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
                () -> bankValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @ParameterizedTest
    @EmptySource
    @MethodSource("getNameArguments")
    @DisplayName("test doFilter should throw ValidationException with name validation fail message")
    void testDoFilterShouldThrowValidationExceptionWithNameValidationFailMessage(String name) {
        BankRequest bankRequest = BankRequestTestBuilder.aBankRequest()
                .withName(name)
                .build();
        String json = gson.toJson(bankRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"name","exception":"Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ @_-]+$"}]}""";

        doReturn("PUT")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class,
                () -> bankValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @ParameterizedTest
    @EmptySource
    @MethodSource("getAddressArguments")
    @DisplayName("test doFilter should throw ValidationException with address validation fail message")
    void testDoFilterShouldThrowValidationExceptionWithAddressValidationFailMessage(String address) {
        BankRequest bankRequest = BankRequestTestBuilder.aBankRequest()
                .withAddress(address)
                .build();
        String json = gson.toJson(bankRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"address","exception":"Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ0-9 .,-]+$"}]}""";

        doReturn("PUT")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class,
                () -> bankValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @ParameterizedTest
    @EmptySource
    @MethodSource("getPhoneNumberArguments")
    @DisplayName("test doFilter should throw ValidationException with phoneNumber validation fail message")
    void testDoFilterShouldThrowValidationExceptionWithPhoneNumberValidationFailMessage(String phoneNumber) {
        BankRequest bankRequest = BankRequestTestBuilder.aBankRequest()
                .withPhoneNumber(phoneNumber)
                .build();
        String json = gson.toJson(bankRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"phone_number","exception":"Field is out of pattern: \
                ^\\\\+\\\\d{1,3} \\\\(\\\\d{1,3}\\\\) \\\\d{3}-\\\\d{2}-\\\\d{2}$"}]}""";

        doReturn("PUT")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class,
                () -> bankValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    private static Stream<Arguments> getNameArguments() {
        return Stream.of(Arguments.of("Bank3000"),
                Arguments.of("?"),
                Arguments.of("<<Great-Bank>>"),
                Arguments.of("$USD$BANK"));
    }

    private static Stream<Arguments> getAddressArguments() {
        return Stream.of(Arguments.of("ул. Все дорожная с долларами $"),
                Arguments.of("?"),
                Arguments.of("<<street. Chicago>>"),
                Arguments.of("ул_Грибоедова_122"));
    }

    private static Stream<Arguments> getPhoneNumberArguments() {
        return Stream.of(Arguments.of("102"),
                Arguments.of("afarama"),
                Arguments.of("+375 (44)2326262"),
                Arguments.of("+375 (44) 232-6262"),
                Arguments.of("+ (44) 232-62-62"),
                Arguments.of("+7 () 232-62-62"),
                Arguments.of("+7 (123) 232 62 62"),
                Arguments.of("+9 (1245) 232-62-62"));
    }

}
