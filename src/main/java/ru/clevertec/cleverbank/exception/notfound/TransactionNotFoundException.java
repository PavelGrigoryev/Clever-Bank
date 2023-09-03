package ru.clevertec.cleverbank.exception.notfound;

public class TransactionNotFoundException extends NotFoundException {

    public TransactionNotFoundException(String message) {
        super(message);
    }

}
