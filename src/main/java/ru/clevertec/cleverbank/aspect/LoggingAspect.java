package ru.clevertec.cleverbank.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;

@Slf4j
@Aspect
public class LoggingAspect {

    @Around("ru.clevertec.cleverbank.aspect.PointCuts.isMethodWithServiceLoggableAnnotation()")
    public Object loggingServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed(joinPoint.getArgs());
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        String logMessage = """
                %s.%s :
                Request : %s
                Response : %s""".formatted(
                className,
                methodName,
                Arrays.toString(joinPoint.getArgs()),
                result);

        log.info(logMessage);
        return result;
    }

    @AfterThrowing(pointcut = "ru.clevertec.cleverbank.aspect.PointCuts.isMethodWithExceptionLoggableAnnotation()", throwing = "e")
    public void loggingException(JoinPoint joinPoint, Throwable e) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        String logErrorMessage = """
                %s.%s :
                Response : %s: %s""".formatted(
                className,
                methodName,
                e.getClass().getSimpleName(),
                e.getMessage());

        log.error(logErrorMessage);
    }

}
