package ru.clevertec.cleverbank.aspect;

import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private Signature signature;

    @Test
    @SneakyThrows
    @DisplayName("test loggingServiceMethod should proceed result")
    void testLoggingServiceMethod() {
        Object expectedResult = "25";
        String className = "String";
        String methodName = "destroyHumans";
        Object[] results = {expectedResult};


        doReturn(results)
                .when(joinPoint)
                .getArgs();
        doReturn(expectedResult)
                .when(joinPoint)
                .proceed(results);
        doReturn(className)
                .when(joinPoint)
                .getTarget();
        doReturn(signature)
                .when(joinPoint)
                .getSignature();
        doReturn(methodName)
                .when(signature)
                .getName();

        Object actualResult = loggingAspect.loggingServiceMethod(joinPoint);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @SneakyThrows
    @DisplayName("test loggingException should proceed result")
    void testLoggingException() {
        String className = "String";
        String methodName = "destroyHumans";

        doReturn(className)
                .when(joinPoint)
                .getTarget();
        doReturn(signature)
                .when(joinPoint)
                .getSignature();
        doReturn(methodName)
                .when(signature)
                .getName();

        loggingAspect.loggingException(joinPoint, new RuntimeException("Hello world!"));

        verify(joinPoint).getTarget();
        verify(joinPoint).getSignature();
        verify(signature).getName();
    }

}
