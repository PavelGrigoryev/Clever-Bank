package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.transaction.AmountStatementResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.ChangeBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionRequestTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionStatementRequestTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionStatementResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransferBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.dto.transaction.AmountStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.TransactionService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServletTest {

    @InjectMocks
    private TransactionServlet transactionServlet;
    @Mock
    private TransactionService transactionService;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private AsyncContext asyncContext;
    @Mock
    private ServletRequest servletRequest;
    @Captor
    private ArgumentCaptor<String> captor;
    @Spy
    private Gson gson;
    private CountDownLatch latch;

    @Nested
    class RepeatedAsyncTest {

        @BeforeEach
        void setUp() {
            latch = new CountDownLatch(1);
        }

        @SneakyThrows
        @RepeatedTest(5)
        @DisplayName("test doPost TransactionRequest should capture expected json from PrintWriter and status 201")
        void testDoPostTransactionRequestShouldCaptureExpectedJsonFromPrintWriter() {
            TransactionRequest request = TransactionRequestTestBuilder.aTransactionRequest().build();
            ChangeBalanceResponse response = ChangeBalanceResponseTestBuilder.aChangeBalanceResponse().build();
            String expectedJson = gson.toJson(response);

            doReturn(asyncContext)
                    .when(req)
                    .startAsync();
            doReturn(request)
                    .when(servletRequest)
                    .getAttribute("changeBalanceRequest");
            doReturn(servletRequest)
                    .when(asyncContext)
                    .getRequest();
            doReturn(response)
                    .when(transactionService)
                    .changeBalance(request);
            doReturn(resp)
                    .when(asyncContext)
                    .getResponse();
            doReturn(printWriter)
                    .when(resp)
                    .getWriter();
            doAnswer(invocation -> {
                latch.countDown();
                return null;
            })
                    .when(asyncContext)
                    .complete();

            transactionServlet.doPost(req, resp);
            latch.await(5, TimeUnit.SECONDS);

            verify(resp).setStatus(201);
            verify(printWriter).print(captor.capture());
            verify(printWriter).flush();

            String actualJson = captor.getValue();

            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @SneakyThrows
        @RepeatedTest(5)
        @DisplayName("test doPost transferBalance should catch SQLException and redirect to exception handler")
        void testDoPostTransferBalanceShouldCatchSQLException() {
            TransactionRequest request = TransactionRequestTestBuilder.aTransactionRequest().build();
            String expectedPath = "/exception_handler";

            doReturn(asyncContext)
                    .when(req)
                    .startAsync();
            doReturn(null)
                    .when(servletRequest)
                    .getAttribute("changeBalanceRequest");
            doReturn(request)
                    .when(servletRequest)
                    .getAttribute("transferBalanceRequest");
            doReturn(servletRequest)
                    .when(asyncContext)
                    .getRequest();
            doThrow(new SQLException("Error message"))
                    .when(transactionService)
                    .transferBalance(request);
            doAnswer(invocation -> {
                latch.countDown();
                return null;
            })
                    .when(asyncContext)
                    .complete();

            transactionServlet.doPost(req, resp);
            latch.await(5, TimeUnit.SECONDS);

            verify(asyncContext).dispatch(captor.capture());

            String actualPath = captor.getValue();

            assertThat(actualPath).isEqualTo(expectedPath);
        }

        @SneakyThrows
        @RepeatedTest(5)
        @DisplayName("test doPost findAllByPeriodOfDateAndAccountId should capture expected json from PrintWriter and status 201")
        void testDoPostFindAllByPeriodOfDateAndAccountIdShouldCaptureExpectedJsonFromPrintWriter() {
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            TransactionStatementResponse response = TransactionStatementResponseTestBuilder.aTransactionStatementResponse().build();
            String expectedJson = gson.toJson(response);

            doReturn(asyncContext)
                    .when(req)
                    .startAsync();
            doReturn(null)
                    .when(servletRequest)
                    .getAttribute("changeBalanceRequest");
            doReturn(request)
                    .when(servletRequest)
                    .getAttribute("statementRequest");
            doReturn(servletRequest)
                    .when(asyncContext)
                    .getRequest();
            doReturn(response)
                    .when(transactionService)
                    .findAllByPeriodOfDateAndAccountId(request);
            doReturn(resp)
                    .when(asyncContext)
                    .getResponse();
            doReturn(printWriter)
                    .when(resp)
                    .getWriter();
            doAnswer(invocation -> {
                latch.countDown();
                return null;
            })
                    .when(asyncContext)
                    .complete();

            transactionServlet.doPost(req, resp);
            latch.await(5, TimeUnit.SECONDS);

            verify(resp).setStatus(201);
            verify(printWriter).print(captor.capture());
            verify(printWriter).flush();

            String actualJson = captor.getValue();

            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @SneakyThrows
        @RepeatedTest(5)
        @DisplayName("test doPut TransactionRequest should capture expected json from PrintWriter and status 201")
        void testDoPutTransactionRequestShouldCaptureExpectedJsonFromPrintWriter() {
            TransactionRequest request = TransactionRequestTestBuilder.aTransactionRequest()
                    .withType(Type.TRANSFER)
                    .build();
            TransferBalanceResponse response = TransferBalanceResponseTestBuilder.aTransferBalanceResponse().build();
            String expectedJson = gson.toJson(response);

            doReturn(asyncContext)
                    .when(req)
                    .startAsync();
            doReturn(request)
                    .when(servletRequest)
                    .getAttribute("transferBalanceRequest");
            doReturn(servletRequest)
                    .when(asyncContext)
                    .getRequest();
            doReturn(response)
                    .when(transactionService)
                    .transferBalance(request);
            doReturn(resp)
                    .when(asyncContext)
                    .getResponse();
            doReturn(printWriter)
                    .when(resp)
                    .getWriter();
            doAnswer(invocation -> {
                latch.countDown();
                return null;
            })
                    .when(asyncContext)
                    .complete();

            transactionServlet.doPut(req, resp);
            latch.await(5, TimeUnit.SECONDS);

            verify(resp).setStatus(201);
            verify(printWriter).print(captor.capture());
            verify(printWriter).flush();

            String actualJson = captor.getValue();

            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @SneakyThrows
        @RepeatedTest(5)
        @DisplayName("test doPut should capture expected json from PrintWriter and status 201")
        void testDoPutShouldCaptureExpectedJsonFromPrintWriter() {
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            AmountStatementResponse response = AmountStatementResponseTestBuilder.aAmountStatementResponse().build();
            String expectedJson = gson.toJson(response);

            doReturn(asyncContext)
                    .when(req)
                    .startAsync();
            doReturn(null)
                    .when(servletRequest)
                    .getAttribute("transferBalanceRequest");
            doReturn(request)
                    .when(servletRequest)
                    .getAttribute("amountRequest");
            doReturn(servletRequest)
                    .when(asyncContext)
                    .getRequest();
            doReturn(response)
                    .when(transactionService)
                    .findSumOfFundsByPeriodOfDateAndAccountId(request);
            doReturn(resp)
                    .when(asyncContext)
                    .getResponse();
            doReturn(printWriter)
                    .when(resp)
                    .getWriter();
            doAnswer(invocation -> {
                latch.countDown();
                return null;
            })
                    .when(asyncContext)
                    .complete();

            transactionServlet.doPut(req, resp);
            latch.await(5, TimeUnit.SECONDS);

            verify(resp).setStatus(201);
            verify(printWriter).print(captor.capture());
            verify(printWriter).flush();

            String actualJson = captor.getValue();

            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @SneakyThrows
        @RepeatedTest(5)
        @DisplayName("test doPut should catch IOException and redirect to exception handler")
        void testDoPutShouldCatchIOException() {
            TransactionStatementRequest request = TransactionStatementRequestTestBuilder.aTransactionStatementRequest().build();
            AmountStatementResponse response = AmountStatementResponseTestBuilder.aAmountStatementResponse().build();
            String expectedPath = "/exception_handler";

            doReturn(asyncContext)
                    .when(req)
                    .startAsync();
            doReturn(request)
                    .when(servletRequest)
                    .getAttribute("amountRequest");
            doReturn(servletRequest)
                    .when(asyncContext)
                    .getRequest();
            doReturn(response)
                    .when(transactionService)
                    .findSumOfFundsByPeriodOfDateAndAccountId(request);
            doReturn(resp)
                    .when(asyncContext)
                    .getResponse();
            doThrow(new IOException("Error Message"))
                    .when(resp)
                    .getWriter();
            doAnswer(invocation -> {
                latch.countDown();
                return null;
            })
                    .when(asyncContext)
                    .complete();

            transactionServlet.doPut(req, resp);
            latch.await(5, TimeUnit.SECONDS);

            verify(asyncContext).dispatch(captor.capture());

            String actualPath = captor.getValue();

            assertThat(actualPath).isEqualTo(expectedPath);
        }

    }

    @Test
    @SneakyThrows
    @DisplayName("test doGet findById should capture expected json from PrintWriter")
    void testDoGetFindByIdShouldCaptureExpectedJsonFromPrintWriter() {
        TransactionResponse response = TransactionResponseTestBuilder.aTransactionResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn("1")
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(response)
                .when(transactionService)
                .findById(response.id());

        transactionServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doGet findAllBySendersAccountId should capture expected json from PrintWriter")
    void testDoGetFindAllBySendersAccountIdShouldCaptureExpectedJsonFromPrintWriter() {
        String id = "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q";
        TransactionResponse response1 = TransactionResponseTestBuilder.aTransactionResponse().build();
        TransactionResponse response2 = TransactionResponseTestBuilder.aTransactionResponse()
                .withId(2L)
                .withAccountSenderId(id)
                .build();
        String expectedJson = gson.toJson(List.of(response1, response2));

        doReturn(null)
                .when(req)
                .getParameter("id");
        doReturn(id)
                .when(req)
                .getParameter("account_sender_id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(List.of(response1, response2))
                .when(transactionService)
                .findAllBySendersAccountId(id);

        transactionServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doGet findAllBySendersAccountId should capture empty json list from PrintWriter")
    void testDoGetFindAllBySendersAccountIdShouldCaptureEmptyJsonListFromPrintWriter() {
        String id = "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q";
        String expectedJson = "[]";

        doReturn(null)
                .when(req)
                .getParameter("id");
        doReturn(id)
                .when(req)
                .getParameter("account_sender_id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(List.of())
                .when(transactionService)
                .findAllBySendersAccountId(id);

        transactionServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doGet findAllByRecipientAccountId should capture expected json from PrintWriter")
    void testDoGetFindAllByRecipientAccountIdShouldCaptureExpectedJsonFromPrintWriter() {
        String id = "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q";
        TransactionResponse response1 = TransactionResponseTestBuilder.aTransactionResponse().build();
        TransactionResponse response2 = TransactionResponseTestBuilder.aTransactionResponse()
                .withId(2L)
                .withAccountSenderId(id)
                .build();
        String expectedJson = gson.toJson(List.of(response1, response2));

        doReturn(null)
                .when(req)
                .getParameter("id");
        doReturn(null)
                .when(req)
                .getParameter("account_sender_id");
        doReturn(id)
                .when(req)
                .getParameter("account_recipient_id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(List.of(response1, response2))
                .when(transactionService)
                .findAllByRecipientAccountId(id);

        transactionServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doGet findAllByRecipientAccountId should capture empty json list from PrintWriter")
    void testDoGetFindAllByRecipientAccountIdShouldCaptureEmptyJsonListFromPrintWriter() {
        String id = "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q";
        String expectedJson = "[]";

        doReturn(null)
                .when(req)
                .getParameter("id");
        doReturn(null)
                .when(req)
                .getParameter("account_sender_id");
        doReturn(id)
                .when(req)
                .getParameter("account_recipient_id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(List.of())
                .when(transactionService)
                .findAllByRecipientAccountId(id);

        transactionServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

}
