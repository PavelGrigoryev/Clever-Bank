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
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.service.UserService;
import ru.clevertec.cleverbank.util.user.UserRequestTestBuilder;
import ru.clevertec.cleverbank.util.user.UserResponseTestBuilder;

import java.io.PrintWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServletTest {

    @InjectMocks
    private UserServlet userServlet;
    @Mock
    private UserService userService;
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
        UserResponse response = UserResponseTestBuilder.aUserResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn("1")
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(response)
                .when(userService)
                .findByIdResponse(response.id());

        userServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doGet findAll should capture expected json list from PrintWriter")
    void testDoGetFindAllShouldCaptureExpectedJsonListFromPrintWriter() {
        UserResponse response1 = UserResponseTestBuilder.aUserResponse().build();
        UserResponse response2 = UserResponseTestBuilder.aUserResponse()
                .withId(2L)
                .withFirstname("James")
                .withLastname("Ocean")
                .build();
        String expectedJson = gson.toJson(List.of(response1, response2));

        doReturn(null)
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(List.of(response1, response2))
                .when(userService)
                .findAll();

        userServlet.doGet(req, resp);

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
                .when(userService)
                .findAll();

        userServlet.doGet(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doPost should capture expected json from PrintWriter and status 201")
    void testDoPostShouldCaptureExpectedJsonFromPrintWriter() {
        UserRequest request = UserRequestTestBuilder.aUserRequest().build();
        UserResponse response = UserResponseTestBuilder.aUserResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn(request)
                .when(req)
                .getAttribute("userRequest");
        doReturn(response)
                .when(userService)
                .save(request);
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        userServlet.doPost(req, resp);

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
        UserRequest request = UserRequestTestBuilder.aUserRequest().build();
        UserResponse response = UserResponseTestBuilder.aUserResponse().build();
        String expectedJson = gson.toJson(response);

        doReturn("1")
                .when(req)
                .getParameter("id");
        doReturn(request)
                .when(req)
                .getAttribute("userRequest");
        doReturn(response)
                .when(userService)
                .update(1L, request);
        doReturn(printWriter)
                .when(resp)
                .getWriter();

        userServlet.doPut(req, resp);

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
        DeleteResponse response = new DeleteResponse("User with ID " + id + " was successfully deleted");
        String expectedJson = gson.toJson(response);

        doReturn("1")
                .when(req)
                .getParameter("id");
        doReturn(printWriter)
                .when(resp)
                .getWriter();
        doReturn(response)
                .when(userService)
                .delete(id);

        userServlet.doDelete(req, resp);

        verify(printWriter).print(captor.capture());
        verify(printWriter).flush();

        String actualJson = captor.getValue();

        assertThat(actualJson).isEqualTo(expectedJson);
    }

}
