package ru.clevertec.cleverbank.exception.badrequest;

public class UniquePhoneNumberException extends  BadRequestException {

    public UniquePhoneNumberException(String message) {
        super(message);
    }

}
