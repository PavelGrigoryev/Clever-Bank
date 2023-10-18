package ru.clevertec.cleverbank.util;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RandomStringGeneratorTest {

    @Test
    @DisplayName("test generateRandomString should return not empty string with expected size")
    void testGenerateRandomString() {
        String actualId = RandomStringGenerator.generateRandomString();

        assertThat(actualId).isNotNull()
                .isNotBlank()
                .isNotEmpty()
                .hasSize(34);
    }

    @Test
    @SneakyThrows
    @DisplayName("test private constructor should throw UnsupportedOperationException with expected message")
    void testPrivateConstructorShouldThrowUnsupportedOperationExceptionWithExpectedMessage() {
        String expectedMessage = "This is a utility class and cannot be instantiated";

        Constructor<?> constructor = RandomStringGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        Throwable cause = exception.getCause();
        String actualMessage = cause.getMessage();

        assertThat(cause).isInstanceOf(UnsupportedOperationException.class);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

}
