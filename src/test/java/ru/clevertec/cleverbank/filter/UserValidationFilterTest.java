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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.user.UserRequestTestBuilder;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;

import java.io.BufferedReader;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserValidationFilterTest {

    @InjectMocks
    private UserValidationFilter userValidationFilter;
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
        UserRequest userRequest = UserRequestTestBuilder.aUserRequest().build();
        String json = gson.toJson(userRequest);

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

        userValidationFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should throw ValidationException with User can not be null message")
    void testDoFilterShouldThrowValidationExceptionWithUserCanNotBeNullMessage() {
        String expectedMessage = """
                {"violations":[{"fieldName":"User","exception":"User can not be null"}]}""";

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn("", (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class, () -> userValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should throw ValidationException with field can not be null message")
    void testDoFilterShouldThrowValidationExceptionWithFieldCanNotBeNullMessage() {
        UserRequest userRequest = UserRequestTestBuilder.aUserRequest()
                .withLastname(null)
                .build();
        String json = gson.toJson(userRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"lastname","exception":"Field can not be null"}]}""";

        doReturn("POST")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class, () -> userValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should throw ValidationException with list of violations")
    void testDoFilterShouldThrowValidationExceptionWithListFoViolations() {
        UserRequest userRequest = UserRequestTestBuilder.aUserRequest()
                .withLastname("")
                .withFirstname(null)
                .withSurname("ZeroKiller2001")
                .withMobileNumber("562-25-258")
                .build();
        String json = gson.toJson(userRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"lastname","exception":"Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ]+$"},\
                {"fieldName":"firstname","exception":"Field can not be null"},\
                {"fieldName":"surname","exception":"Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ]+$"},\
                {"fieldName":"mobile_number","exception":"Field is out of pattern: \
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

        Exception exception = assertThrows(ValidationException.class, () -> userValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @ParameterizedTest
    @EmptySource
    @MethodSource("getNameArguments")
    @DisplayName("test doFilter should throw ValidationException with lastname validation fail message")
    void testDoFilterShouldThrowValidationExceptionWithLastnameValidationFailMessage(String lastname) {
        UserRequest userRequest = UserRequestTestBuilder.aUserRequest()
                .withLastname(lastname)
                .build();
        String json = gson.toJson(userRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"lastname","exception":"Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ]+$"}]}""";

        doReturn("PUT")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class, () -> userValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @ParameterizedTest
    @EmptySource
    @MethodSource("getNameArguments")
    @DisplayName("test doFilter should throw ValidationException with firstname validation fail message")
    void testDoFilterShouldThrowValidationExceptionWithFirstnameValidationFailMessage(String firstname) {
        UserRequest userRequest = UserRequestTestBuilder.aUserRequest()
                .withFirstname(firstname)
                .build();
        String json = gson.toJson(userRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"firstname","exception":"Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ]+$"}]}""";

        doReturn("PUT")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class, () -> userValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @ParameterizedTest
    @EmptySource
    @MethodSource("getNameArguments")
    @DisplayName("test doFilter should throw ValidationException with surname validation fail message")
    void testDoFilterShouldThrowValidationExceptionWithSurnameValidationFailMessage(String surname) {
        UserRequest userRequest = UserRequestTestBuilder.aUserRequest()
                .withSurname(surname)
                .build();
        String json = gson.toJson(userRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"surname","exception":"Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ]+$"}]}""";

        doReturn("PUT")
                .when(req)
                .getMethod();
        doReturn(bufferedReader)
                .when(req)
                .getReader();
        doReturn(json, (Object) null)
                .when(bufferedReader)
                .readLine();

        Exception exception = assertThrows(ValidationException.class, () -> userValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @ParameterizedTest
    @EmptySource
    @MethodSource("getMobileNumberArguments")
    @DisplayName("test doFilter should throw ValidationException with mobileNumber validation fail message")
    void testDoFilterShouldThrowValidationExceptionWithMobileNumberValidationFailMessage(String mobileNumber) {
        UserRequest userRequest = UserRequestTestBuilder.aUserRequest()
                .withMobileNumber(mobileNumber)
                .build();
        String json = gson.toJson(userRequest);
        String expectedMessage = """
                {"violations":[{"fieldName":"mobile_number","exception":"Field is out of pattern: \
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

        Exception exception = assertThrows(ValidationException.class, () -> userValidationFilter.doFilter(request, response, chain));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    private static Stream<Arguments> getNameArguments() {
        return Stream.of(Arguments.of("Chubaka3000"),
                Arguments.of("Zhan Thang"),
                Arguments.of("Sub-Zero"),
                Arguments.of("007"));
    }

    private static Stream<Arguments> getMobileNumberArguments() {
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
