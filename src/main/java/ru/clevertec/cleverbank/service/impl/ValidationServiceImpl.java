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

    /**
     * Реализует метод validateAccountForClosingDate, который проверяет, что счёт не закрыт по дате закрытия.
     *
     * @param closingDate объект LocalDate, представляющий дату закрытия счёта
     * @param accountId   String, представляющая идентификатор счёта
     * @throws AccountClosedException если счёт закрыт по дате закрытия
     */
    @Override
    public void validateAccountForClosingDate(LocalDate closingDate, String accountId) {
        if (closingDate != null) {
            throw new AccountClosedException("Account with ID " + accountId + " is closed since " + closingDate);
        }
    }

    /**
     * Реализует метод validateAccountForCurrency, который проверяет, что валюта счетов отправителя и получателя одинаковая.
     *
     * @param senderCurrency    объект Currency, представляющий валюту счёта отправителя
     * @param recipientCurrency объект Currency, представляющий валюту счёта получателя
     * @throws BadCurrencyException если валюта счетов отправителя и получателя разная
     */
    @Override
    public void validateAccountForCurrency(Currency senderCurrency, Currency recipientCurrency) {
        if (!senderCurrency.equals(recipientCurrency)) {
            throw new BadCurrencyException("Your currency is " + recipientCurrency
                                           + ", but account currency is " + senderCurrency);
        }
    }

    /**
     * Реализует метод validateAccountForSufficientBalance, который проверяет, что баланс счёта достаточен для
     * выполнения операции (перевод или снятие).
     *
     * @param type       объект Type, представляющий тип операции
     * @param sum        объект BigDecimal, представляющий сумму операции
     * @param oldBalance объект BigDecimal, представляющий старый баланс счета
     * @throws InsufficientFundsException если баланс счета недостаточен для выполнения операции
     */
    @Override
    public void validateAccountForSufficientBalance(Type type, BigDecimal sum, BigDecimal oldBalance) {
        if (type != Type.REPLENISHMENT && oldBalance.compareTo(sum) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the account! You want to withdrawal/transfer "
                                                 + sum + ", but you have only " + oldBalance);
        }
    }

    /**
     * Реализует метод validateFieldByPattern, который проверяет, что поле соответствует заданному шаблону.
     *
     * @param field         String, представляющая поле для проверки
     * @param fieldName     String, представляющая название поля для проверки
     * @param patternString String, представляющая шаблон для проверки поля
     * @param violations    список объектов Violation, в который добавляются нарушения при проверке поля
     */
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

    /**
     * Реализует метод validateRequestForNull, который проверяет, что запрос не равен null.
     *
     * @param object      объект, представляющий запрос для проверки
     * @param requestName String, представляющая название запроса для проверки
     * @param gson        объект Gson, представляющий парсер JSON
     * @throws ValidationException если запрос равен null
     */
    @Override
    public void validateRequestForNull(Object object, String requestName, Gson gson) {
        if (object == null) {
            Violation violation = new Violation(requestName, "%s can not be null".formatted(requestName));
            String validationJson = gson.toJson(new ValidationResponse(List.of(violation)));
            throw new ValidationException(validationJson);
        }
    }

    /**
     * Реализует метод validateBigDecimalFieldForPositive, который проверяет, что поле типа BigDecimal является
     * положительным числом.
     *
     * @param field      объект BigDecimal, представляющий поле для проверки
     * @param fieldName  String, представляющая название поля для проверки
     * @param violations список объектов Violation, в который добавляются нарушения при проверке поля
     */
    @Override
    public void validateBigDecimalFieldForPositive(BigDecimal field, String fieldName, List<Violation> violations) {
        if (field == null) {
            Violation violation = new Violation(fieldName, "Field can not be null");
            violations.add(violation);
        } else if (field.compareTo(BigDecimal.ZERO) <= 0) {
            Violation violation = new Violation(fieldName, "Field must be greater than 0");
            violations.add(violation);
        }
    }

    /**
     * Реализует метод validateLongFieldForPositive, который проверяет, что поле типа Long является положительным числом.
     *
     * @param field      Long, представляющее поле для проверки
     * @param fieldName  String, представляющая название поля для проверки
     * @param violations список объектов Violation, в который добавляются нарушения при проверке поля
     */
    @Override
    public void validateLongFieldForPositive(Long field, String fieldName, List<Violation> violations) {
        if (field == null) {
            Violation violation = new Violation(fieldName, "Field can not be null");
            violations.add(violation);
        } else if (field <= 0) {
            Violation violation = new Violation(fieldName, "Field must be greater than 0");
            violations.add(violation);
        }
    }

    /**
     * Реализует метод validateStringFieldForNullOrEmpty, который проверяет, что поле типа String не null и не пуста.
     *
     * @param accountId  String, представляющее поле для проверки
     * @param fieldName  String, представляющая название поля для проверки
     * @param violations список объектов Violation, в который добавляются нарушения при проверке поля
     */
    @Override
    public void validateAccountId(String accountId, String fieldName, List<Violation> violations) {
        if (accountId == null || accountId.isEmpty() || accountId.isBlank()) {
            Violation violation = new Violation(fieldName, "Field can not be null, blank or empty");
            violations.add(violation);
        }
    }

}
