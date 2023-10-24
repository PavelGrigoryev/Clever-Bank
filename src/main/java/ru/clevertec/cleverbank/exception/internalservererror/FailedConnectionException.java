package ru.clevertec.cleverbank.exception.internalservererror;

public class FailedConnectionException extends InternalServerErrorException {

    public FailedConnectionException(String message) {
        super(message);
    }

}
