package ru.clevertec.cleverbank.exception.internalservererror;

public class TransactionException extends InternalServerErrorException {

    public TransactionException(String message) {
        super(message);
    }

}
