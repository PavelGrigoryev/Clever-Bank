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
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.service.BankService;
import ru.clevertec.cleverbank.builder.bank.BankRequestTestBuilder;
import ru.clevertec.cleverbank.builder.bank.BankResponseTestBuilder;

import java.io.PrintWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankServletTest {

    @InjectMocks
    private BankServlet bankServlet;
    @Mock
    private BankService bankService;
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
        BankResponse response = BankResponseTestBuilder.aBankResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn("1")
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(response)
                .when(bankService)
                .findByIdResponse(response.id());

        bankServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doGet findAll should capture expected json list from PrintWriter")
    void testDoGetFindAllShouldCaptureExpectedJsonListFromPrintWriter() {
        BankResponse response1 = BankResponseTestBuilder.aBankResponse().build();
        BankResponse response2 = BankResponseTestBuilder.aBankResponse()
                .withId(2L)
                .withName("Average Bank")
                .withPhoneNumber("102")
                .build();
        String expectedJson = gson.toJson(List.of(response1, response2));

        doReturn(null)
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(List.of(response1, response2))
                .when(bankService)
                .findAll();

        bankServlet.doGet(req, resp);

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
                .when(bankService)
                .findAll();

        bankServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }


    @Test
    @SneakyThrows
    @DisplayName("test doPost should capture expected json from PrintWriter and status 201")
    void testDoPostShouldCaptureExpectedJsonFromPrintWriter() {
        BankRequest request = BankRequestTestBuilder.aBankRequest().build();
        BankResponse response = BankResponseTestBuilder.aBankResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn(request)
                .when(req)
                .getAttribute("bankRequest");
        doReturn(response)
                .when(bankService)
                .save(request);
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        bankServlet.doPost(req, resp);

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
        BankRequest request = BankRequestTestBuilder.aBankRequest().build();
        BankResponse response = BankResponseTestBuilder.aBankResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn("1")
                .when(req)
                .getParameter("id");
        doReturn(request)
                .when(req)
                .getAttribute("bankRequest");
        doReturn(response)
                .when(bankService)
                .update(1L, request);
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        bankServlet.doPut(req, resp);

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
        long id = 1L;
        DeleteResponse response = new DeleteResponse("Bank with ID " + id + " was successfully deleted");
        String expectedJson = gson.toJson(response);

        doReturn("1")
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(response)
                .when(bankService)
                .delete(id);

        bankServlet.doDelete(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

}
