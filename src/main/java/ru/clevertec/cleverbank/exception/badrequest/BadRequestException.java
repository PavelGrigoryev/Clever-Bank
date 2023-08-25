package ru.clevertec.cleverbank.exception.badrequest;

public abstract class BadRequestException extends RuntimeException {

    protected BadRequestException(String message) {
        super(message);
    }

}
