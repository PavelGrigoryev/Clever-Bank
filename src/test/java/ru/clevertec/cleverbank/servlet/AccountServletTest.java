package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
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
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.util.account.AccountRequestTestBuilder;
import ru.clevertec.cleverbank.util.account.AccountResponseTestBuilder;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServletTest {

    @InjectMocks
    private AccountServlet accountServlet;
    @Mock
    private AccountService accountService;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private PrintWriter printWriter;
    @Captor
    private ArgumentCaptor<String> captor;
    @Spy
    private Gson gson;

    @Test
    @SneakyThrows
    @DisplayName("test doGet findById should capture expected json from PrintWriter")
    void testDoGetFindByIdShouldCaptureExpectedJsonFromPrintWriter() {
        AccountResponse response = AccountResponseTestBuilder.aAccountResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn(response.id())
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(response)
                .when(accountService)
                .findByIdResponse(response.id());

        accountServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doGet findAll should capture expected json list from PrintWriter")
    void testDoGetFindAllShouldCaptureExpectedJsonListFromPrintWriter() {
        AccountResponse response1 = AccountResponseTestBuilder.aAccountResponse().build();
        AccountResponse response2 = AccountResponseTestBuilder.aAccountResponse()
                .withId("HK5H 7CEV LQKJ XSF6 WGEL 5AMZ QXVA")
                .withCurrency(Currency.RUB)
                .withBalance(BigDecimal.TEN)
                .build();
        String expectedJson = gson.toJson(List.of(response1, response2));

        doReturn(null)
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(List.of(response1, response2))
                .when(accountService)
                .findAllResponses();

        accountServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doGet findAll should capture empty json list from PrintWriter")
    void testDoGetFindAllShouldCaptureEmptyJsonListFromPrintWriter() {
        String expectedJson = "[]";

        doReturn(null)
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(List.of())
                .when(accountService)
                .findAllResponses();

        accountServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }


    @Test
    @SneakyThrows
    @DisplayName("test doPost should capture expected json from PrintWriter and status 201")
    void testDoPostShouldCaptureExpectedJsonFromPrintWriter() {
        AccountRequest request = AccountRequestTestBuilder.aAccountRequest().build();
        AccountResponse response = AccountResponseTestBuilder.aAccountResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn(request)
                .when(req)
                .getAttribute("accountRequest");
        doReturn(response)
                .when(accountService)
                .save(request);
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        accountServlet.doPost(req, resp);

        verify(resp).setStatus(201);
        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doPut should capture expected json from PrintWriter and status 201")
    void testDoPutShouldCaptureExpectedJsonFromPrintWriter() {
        AccountResponse response = AccountResponseTestBuilder.aAccountResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn(response.id())
                .when(req)
                .getParameter("id");
        doReturn(response)
                .when(accountService)
                .closeAccount(response.id());
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        accountServlet.doPut(req, resp);

        verify(resp).setStatus(201);
        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doDelete should capture expected json from PrintWriter")
    void testDoDeleteShouldCaptureExpectedJsonFromPrintWriter() {
        String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
        DeleteResponse response = new DeleteResponse("Account with ID " + id + " was successfully deleted");
        String expectedJson = gson.toJson(response);

        doReturn(id)
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(response)
                .when(accountService)
                .delete(id);

        accountServlet.doDelete(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

}
