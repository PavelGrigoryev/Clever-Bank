package ru.clevertec.cleverbank.exception;

public class InsufficientFundsException extends BadRequestException {

    public InsufficientFundsException(String message) {
        super(message);
    }

}
