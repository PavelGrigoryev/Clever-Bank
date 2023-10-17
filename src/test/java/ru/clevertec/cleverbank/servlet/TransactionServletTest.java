package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.service.TransactionService;
import ru.clevertec.cleverbank.util.transaction.ChangeBalanceRequestTestBuilder;
import ru.clevertec.cleverbank.util.transaction.ChangeBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransferBalanceRequestTestBuilder;
import ru.clevertec.cleverbank.util.transaction.TransferBalanceResponseTestBuilder;

import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
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

    @Test
    @SneakyThrows
    @DisplayName("test doPost changeBalance should capture expected json from PrintWriter and status 201")
    void testDoPostChangeBalanceShouldCaptureExpectedJsonFromPrintWriter() {
        ChangeBalanceRequest request = ChangeBalanceRequestTestBuilder.aChangeBalanceRequest().build();
        ChangeBalanceResponse response = ChangeBalanceResponseTestBuilder.aChangeBalanceResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn(asyncContext)
                .when(req)
                .startAsync();
        doReturn(request)
                .when(servletRequest)
                .getAttribute("changeBalanceRequest");
        doReturn(null)
                .when(servletRequest)
                .getAttribute("transferBalanceRequest");
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

        transactionServlet.doPost(req, resp);

        verify(resp).setStatus(201);
        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doPost transferBalance should capture expected json from PrintWriter and status 201")
    void testDoPostTransferBalanceShouldCaptureExpectedJsonFromPrintWriter() {
        TransferBalanceRequest request = TransferBalanceRequestTestBuilder.aTransferBalanceRequest().build();
        TransferBalanceResponse response = TransferBalanceResponseTestBuilder.aTransferBalanceResponse().build();
        String expectedJson = gson.toJson(response);

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
        doReturn(response)
                .when(transactionService)
                .transferBalance(request);
        doReturn(resp)
                .when(asyncContext)
                .getResponse();
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        transactionServlet.doPost(req, resp);

        verify(resp).setStatus(201);
        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

}
