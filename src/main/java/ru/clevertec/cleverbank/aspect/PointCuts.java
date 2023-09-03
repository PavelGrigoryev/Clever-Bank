package ru.clevertec.cleverbank.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class PointCuts {

    @Pointcut("@annotation(ru.clevertec.cleverbank.aspect.annotation.ServiceLoggable)")
    public void isMethodWithServiceLoggableAnnotation() {
    }

    @Pointcut("@annotation(ru.clevertec.cleverbank.aspect.annotation.ExceptionLoggable)")
    public void isMethodWithExceptionLoggableAnnotation() {
    }

}
