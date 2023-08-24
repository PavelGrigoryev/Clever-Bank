package ru.clevertec.cleverbank.exception;

public abstract class BadRequestException extends RuntimeException {

    protected BadRequestException(String message) {
        super(message);
    }

}
