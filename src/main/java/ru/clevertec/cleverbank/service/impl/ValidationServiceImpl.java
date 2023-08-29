package ru.clevertec.cleverbank.service.impl;

import com.google.gson.Gson;
import ru.clevertec.cleverbank.exception.badrequest.AccountClosedException;
import ru.clevertec.cleverbank.exception.badrequest.BadCurrencyException;
import ru.clevertec.cleverbank.exception.badrequest.InsufficientFundsException;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;
import ru.clevertec.cleverbank.exception.handler.ValidationResponse;
import ru.clevertec.cleverbank.exception.handler.Violation;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.ValidationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationServiceImpl implements ValidationService {

    @Override
    public void validateAccountForClosingDate(LocalDate closingDate, String accountId) {
        if (closingDate != null) {
            throw new AccountClosedException("Account with ID " + accountId + " is closed since " + closingDate);
        }
    }

    @Override
    public void validateAccountForCurrency(Currency senderCurrency, Currency resipientCurrency) {
        if (!senderCurrency.equals(resipientCurrency)) {
            throw new BadCurrencyException("Your currency is " + resipientCurrency
                                           + ", but account currency is " + senderCurrency);
        }
    }

    @Override
    public void validateAccountForSufficientBalance(Type type, BigDecimal sum, BigDecimal oldBalance) {
        if (type != Type.REPLENISHMENT && oldBalance.compareTo(sum) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the account! You want to withdrawal/transfer "
                                                 + sum + ", but you have only " + oldBalance);
        }
    }

    @Override
    public void validateFieldByPattern(String field, String fieldName, String patternString, List<Violation> violations) {
        if (field == null) {
            Violation violation = new Violation(fieldName, "Field can not be null");
            violations.add(violation);
        } else {
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(field);
            if (!matcher.matches()) {
                Violation violation = new Violation(fieldName, "Field is out of pattern: " + patternString);
                violations.add(violation);
            }
        }
    }

    @Override
    public void validateRequestForNull(Object object, String requestName, Gson gson) {
        if (object == null) {
            Violation violation = new Violation(requestName, "%s can not be null".formatted(requestName));
            String validationJson = gson.toJson(new ValidationResponse(List.of(violation)));
            throw new ValidationException(validationJson);
        }
    }

    @Override
    public void validateBigDecimalFieldForPositive(BigDecimal field, String fieldName, List<Violation> violations) {
        if (field == null) {
            Violation violation = new Violation(fieldName, "Field can not be null");
            violations.add(violation);
        } else if (field.compareTo(BigDecimal.ZERO) <= 0) {
            Violation violation = new Violation(fieldName, "Field must be grater than 0");
            violations.add(violation);
        }
    }

    @Override
    public void validateLongFieldForPositive(Long field, String fieldName, List<Violation> violations) {
        if (field == null) {
            Violation violation = new Violation(fieldName, "Field can not be null");
            violations.add(violation);
        } else if (field <= 0) {
            Violation violation = new Violation(fieldName, "Field must be grater than 0");
            violations.add(violation);
        }
    }

}
