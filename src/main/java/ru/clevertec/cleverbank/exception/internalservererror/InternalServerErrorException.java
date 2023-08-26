package ru.clevertec.cleverbank.exception.internalservererror;

public abstract class InternalServerErrorException extends RuntimeException {

    private static final String MESSAGE = "Sorry! We got Server database connection problems";

    protected InternalServerErrorException() {
        super(MESSAGE);
    }

    protected InternalServerErrorException(String message) {
        super(message);
    }

}
