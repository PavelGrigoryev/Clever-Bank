package ru.clevertec.cleverbank.exception;

public class AccountClosedException extends BadRequestException {

    public AccountClosedException(String message) {
        super(message);
    }

}
