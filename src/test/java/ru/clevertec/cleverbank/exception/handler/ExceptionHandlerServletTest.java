package ru.clevertec.cleverbank.exception.handler;

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
import ru.clevertec.cleverbank.exception.badrequest.AccountClosedException;
import ru.clevertec.cleverbank.exception.badrequest.BadRequestException;
import ru.clevertec.cleverbank.exception.conflict.LocalDateParseException;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;
import ru.clevertec.cleverbank.exception.internalservererror.InternalServerErrorException;
import ru.clevertec.cleverbank.exception.internalservererror.UploadFileException;
import ru.clevertec.cleverbank.exception.notfound.NotFoundException;
import ru.clevertec.cleverbank.exception.notfound.UserNotFoundException;

import java.io.PrintWriter;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerServletTest {

    @InjectMocks
    private ExceptionHandlerServlet exceptionHandlerServlet;
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
    @DisplayName("test service should capture expected json from PrintWriter and status 404 from NotFoundException")
    void testServiceShouldCaptureExpectedJsonAndStatus404FromNotFoundException() {
        String exceptionMessage = "User is not found!";
        ExceptionResponse response = new ExceptionResponse(exceptionMessage);
        NotFoundException notFoundException = new UserNotFoundException(exceptionMessage);
        String expectedJson = gson.toJson(response);

        doNothing()
                .when(resp)
                .setContentType("application/json");
        doNothing()
                .when(resp)
                .setCharacterEncoding("UTF-8");
        doReturn(notFoundException)
                .when(req)
                .getAttribute(ERROR_EXCEPTION);
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        exceptionHandlerServlet.service(req, resp);

        verify(resp).setStatus(404);
        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test service should capture expected json from PrintWriter and status 400 from BadRequestException")
    void testServiceShouldCaptureExpectedJsonAndStatus400FromBadRequestException() {
        String exceptionMessage = "This account is closed!";
        ExceptionResponse response = new ExceptionResponse(exceptionMessage);
        BadRequestException badRequestException = new AccountClosedException(exceptionMessage);
        String expectedJson = gson.toJson(response);

        doNothing()
                .when(resp)
                .setContentType("application/json");
        doNothing()
                .when(resp)
                .setCharacterEncoding("UTF-8");
        doReturn(badRequestException)
                .when(req)
                .getAttribute(ERROR_EXCEPTION);
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        exceptionHandlerServlet.service(req, resp);

        verify(resp).setStatus(400);
        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test service should capture expected json from PrintWriter and status 409 from ValidationException")
    void testServiceShouldCaptureExpectedJsonAndStatus409FromValidationException() {
        String expectedMessage = "This field is not valid!";
        ValidationException validationException = new ValidationException(expectedMessage);

        doNothing()
                .when(resp)
                .setContentType("application/json");
        doNothing()
                .when(resp)
                .setCharacterEncoding("UTF-8");
        doReturn(validationException)
                .when(req)
                .getAttribute(ERROR_EXCEPTION);
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        exceptionHandlerServlet.service(req, resp);

        verify(resp).setStatus(409);
        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualMessage = captor.getValue();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("test service should capture expected json from PrintWriter and status 409 from LocalDateParseException")
    void testServiceShouldCaptureExpectedJsonAndStatus409FromLocalDateParseException() {
        String exceptionMessage = "This date is wrong!";
        ExceptionResponse response = new ExceptionResponse(exceptionMessage);
        ValidationException validationException = new LocalDateParseException(exceptionMessage);
        String expectedJson = gson.toJson(response);

        doNothing()
                .when(resp)
                .setContentType("application/json");
        doNothing()
                .when(resp)
                .setCharacterEncoding("UTF-8");
        doReturn(validationException)
                .when(req)
                .getAttribute(ERROR_EXCEPTION);
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        exceptionHandlerServlet.service(req, resp);

        verify(resp).setStatus(409);
        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test service should capture expected json from PrintWriter and status 500 from InternalServerErrorException")
    void testServiceShouldCaptureExpectedJsonAndStatus500FromInternalServerErrorException() {
        String exceptionMessage = "This file is bad!";
        ExceptionResponse response = new ExceptionResponse(exceptionMessage);
        InternalServerErrorException internalServerErrorException = new UploadFileException(exceptionMessage);
        String expectedJson = gson.toJson(response);

        doNothing()
                .when(resp)
                .setContentType("application/json");
        doNothing()
                .when(resp)
                .setCharacterEncoding("UTF-8");
        doReturn(internalServerErrorException)
                .when(req)
                .getAttribute(ERROR_EXCEPTION);
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        exceptionHandlerServlet.service(req, resp);

        verify(resp).setStatus(500);
        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

}
