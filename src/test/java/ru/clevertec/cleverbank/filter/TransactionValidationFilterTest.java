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
import ru.clevertec.cleverbank.builder.transaction.ChangeBalanceRequestTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionStatementRequestTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransferBalanceRequestTestBuilder;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceRequest;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;
import ru.clevertec.cleverbank.model.Type;

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
class TransactionValidationFilterTest {

    @InjectMocks
    private TransactionValidationFilter transactionValidationFilter;
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
        ChangeBalanceRequest changeBalanceRequest = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest().build();
        String json = gson.toJson(changeBalanceRequest);

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

        transactionValidationFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should capture ChangeBalanceRequest values")
    void testDoFilterShouldCaptureChangeBalanceRequestValues() {
        ChangeBalanceRequest expectedChangeBalance = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest().build();
        String json = gson.toJson(expectedChangeBalance);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ChangeBalanceRequest> changeBalanceRequestCaptor = ArgumentCaptor.forClass(ChangeBalanceRequest.class);
        String expectedString = "changeBalanceRequest";

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        transactionValidationFilter.doFilter(request, response, chain);

        verify(req).setAttribute(stringCaptor.capture(), changeBalanceRequestCaptor.capture());

        String actualString = stringCaptor.getValue();
        ChangeBalanceRequest actualChangeBalance = changeBalanceRequestCaptor.getValue();

        assertAll(
                () -> assertThat(actualString).isEqualTo(expectedString),
                () -> assertThat(actualChangeBalance).isEqualTo(expectedChangeBalance)
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should capture TransferBalanceRequest values")
    void testDoFilterShouldCaptureTransferBalanceRequestValues() {
        TransferBalanceRequest expectedTransferBalance = TransferBalanceRequestTestBuilder.aTransferBalanceRequest().build();
        String json = gson.toJson(expectedTransferBalance);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TransferBalanceRequest> transferBalanceCaptor = ArgumentCaptor
                .forClass(TransferBalanceRequest.class);
        String expectedString = "transferBalanceRequest";

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        transactionValidationFilter.doFilter(request, response, chain);

        verify(req).setAttribute(stringCaptor.capture(), transferBalanceCaptor.capture());

        String actualString = stringCaptor.getValue();
        TransferBalanceRequest actualTransferBalance = transferBalanceCaptor.getValue();

        assertAll(
                () -> assertThat(actualString).isEqualTo(expectedString),
                () -> assertThat(actualTransferBalance).isEqualTo(expectedTransferBalance)
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should capture TransactionStatementRequest values")
    void testDoFilterShouldCaptureTransactionStatementRequestValues() {
        TransactionStatementRequest expectedTransactionStatement = TransactionStatementRequestTestBuilder
                .aTransactionStatementRequest().build();
        String json = gson.toJson(expectedTransactionStatement);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TransactionStatementRequest> transactionStatementCaptor = ArgumentCaptor
                .forClass(TransactionStatementRequest.class);
        String expectedString = "statementRequest";

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        transactionValidationFilter.doFilter(request, response, chain);

        verify(req).setAttribute(stringCaptor.capture(), transactionStatementCaptor.capture());

        String actualString = stringCaptor.getValue();
        TransactionStatementRequest actualTransactionStatement = transactionStatementCaptor.getValue();

        assertAll(
                () -> assertThat(actualString).isEqualTo(expectedString),
                () -> assertThat(actualTransactionStatement).isEqualTo(expectedTransactionStatement)
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should capture AmountRequest values")
    void testDoFilterShouldCaptureAmountRequestValues() {
        TransactionStatementRequest expectedTransactionStatement = TransactionStatementRequestTestBuilder
                .aTransactionStatementRequest().build();
        String json = gson.toJson(expectedTransactionStatement);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TransactionStatementRequest> transactionStatementCaptor = ArgumentCaptor
                .forClass(TransactionStatementRequest.class);
        String expectedString = "amountRequest";

        doReturn("PUT")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        transactionValidationFilter.doFilter(request, response, chain);

        verify(req).setAttribute(stringCaptor.capture(), transactionStatementCaptor.capture());

        String actualString = stringCaptor.getValue();
        TransactionStatementRequest actualTransactionStatement = transactionStatementCaptor.getValue();

        assertAll(
                () -> assertThat(actualString).isEqualTo(expectedString),
                () -> assertThat(actualTransactionStatement).isEqualTo(expectedTransactionStatement)
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter POST should throw ValidationException with Transaction can not be null message")
    void testDoFilterPOSTShouldThrowValidationExceptionWithTransactionCanNotBeNullMessage() {
        String expectedMessage = """
                {"violations":[{"fieldName":"Transaction","exception":"Transaction can not be null"}]}""";

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
                () -> transactionValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter PUT should throw ValidationException with Transaction can not be null message")
    void testDoFilterPUTShouldThrowValidationExceptionWithTransactionCanNotBeNullMessage() {
        String expectedMessage = """
                {"violations":[{"fieldName":"Transaction","exception":"Transaction can not be null"}]}""";

        doReturn("PUT")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn("", (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class,
                () -> transactionValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter POST should throw ValidationException with field can not be null message")
    void testDoFilterPOSTShouldThrowValidationExceptionWithFieldCanNotBeNullMessage() {
        ChangeBalanceRequest changeBalanceRequest = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                .withSum(null)
                .build();
        String json = gson.toJson(changeBalanceRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"sum","exception":"Field can not be null"}]}""";

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
                () -> transactionValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter PUT should throw ValidationException with field can not be null message")
    void testDoFilterPUTShouldThrowValidationExceptionWithFieldCanNotBeNullMessage() {
        TransactionStatementRequest transactionStatementRequest = TransactionStatementRequestTestBuilder
                .aTransactionStatementRequest()
                .withAccountId(null)
                .build();
        String json = gson.toJson(transactionStatementRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"account_id","exception":"Field can not be null, blank or empty"}]}""";

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
                () -> transactionValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getChangeBalanceRequestArguments")
    @DisplayName("test doFilter should throw ValidationException with list of violations for ChangeBalanceRequest")
    void testDoFilterShouldThrowValidationExceptionWithListOfViolationsForChangeBalanceRequest(ChangeBalanceRequest changeBalanceRequest) {
        String json = gson.toJson(changeBalanceRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"account_recipient_id","exception":"Field can not be null, blank or empty"},\
                {"fieldName":"account_sender_id","exception":"Field can not be null, blank or empty"},\
                {"fieldName":"type","exception":"Available types are: REPLENISHMENT or WITHDRAWAL"},\
                {"fieldName":"sum","exception":"Field must be greater than 0"}]}""";

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
                () -> transactionValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getTransferBalanceRequestArguments")
    @DisplayName("test doFilter should throw ValidationException with list of violations for TransferBalanceRequest")
    void testDoFilterShouldThrowValidationExceptionWithListOfViolationsForTransferBalanceRequest(TransferBalanceRequest transferBalanceRequest) {
        String json = gson.toJson(transferBalanceRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"account_recipient_id","exception":"Field can not be null, blank or empty"},\
                {"fieldName":"account_sender_id","exception":"Field can not be null, blank or empty"},\
                {"fieldName":"sum","exception":"Field must be greater than 0"}]}""";

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
                () -> transactionValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    private static Stream<Arguments> getChangeBalanceRequestArguments() {
        return Stream.of(Arguments.of(ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                        .withType(Type.TRANSFER)
                        .withSum(BigDecimal.ZERO)
                        .withAccountSenderId("")
                        .withAccountRecipientId(null)
                        .build()),
                Arguments.of(ChangeBalanceRequestTestBuilder.aChangeBalanceRequest()
                        .withType(Type.TRANSFER)
                        .withSum(BigDecimal.valueOf(-5))
                        .withAccountSenderId(" ")
                        .withAccountRecipientId("")
                        .build()));
    }

    private static Stream<Arguments> getTransferBalanceRequestArguments() {
        return Stream.of(Arguments.of(TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                        .withSum(BigDecimal.ZERO)
                        .withAccountSenderId("  ")
                        .withAccountRecipientId(null)
                        .build()),
                Arguments.of(TransferBalanceRequestTestBuilder.aTransferBalanceRequest()
                        .withSum(BigDecimal.valueOf(-66))
                        .withAccountSenderId("")
                        .withAccountRecipientId("")
                        .build()));
    }

}
