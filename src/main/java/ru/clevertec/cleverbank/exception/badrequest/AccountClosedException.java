package ru.clevertec.cleverbank.exception.badrequest;

public class AccountClosedException extends BadRequestException {

    public AccountClosedException(String message) {
        super(message);
    }

}
