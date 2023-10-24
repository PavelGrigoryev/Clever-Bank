package ru.clevertec.cleverbank.service.impl;

import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.exception.badrequest.AccountClosedException;
import ru.clevertec.cleverbank.exception.badrequest.BadCurrencyException;
import ru.clevertec.cleverbank.exception.badrequest.InsufficientFundsException;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;
import ru.clevertec.cleverbank.exception.handler.ValidationResponse;
import ru.clevertec.cleverbank.exception.handler.Violation;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @InjectMocks
    private ValidationServiceImpl validationService;
    @Mock
    private Gson gson;

    @Test
    @DisplayName("test validateAccountForClosingDate should throw AccountClosedException with expected message")
    void testValidateAccountForClosingDateShouldThrowAccountClosedExceptionWithExpectedMessage() {
        LocalDate date = LocalDate.of(2020, Month.APRIL, 12);
        String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
        String expectedMessage = "Account with ID " + id + " is closed since " + date;

        Exception exception = assertThrows(AccountClosedException.class,
                () -> validationService.validateAccountForClosingDate(date, id));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("test validateAccountForCurrency should throw BadCurrencyException with expected message")
    void testValidateAccountForCurrencyShouldThrowBadCurrencyExceptionWithExpectedMessage() {
        Currency senderCurrency = Currency.BYN;
        Currency recipientCurrency = Currency.EUR;
        String expectedMessage = "Your currency is " + recipientCurrency + ", but account currency is " + senderCurrency;

        Exception exception = assertThrows(BadCurrencyException.class,
                () -> validationService.validateAccountForCurrency(senderCurrency, recipientCurrency));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("test validateAccountForSufficientBalance should throw InsufficientFundsException with expected message")
    void testValidateAccountForSufficientBalanceShouldThrowInsufficientFundsExceptionWithExpectedMessage() {
        Type type = Type.WITHDRAWAL;
        BigDecimal sum = BigDecimal.TEN;
        BigDecimal oldBalance = BigDecimal.valueOf(-1);

        String expectedMessage = "Insufficient funds in the account! You want to change balance "
                                 + sum + ", but you have only " + oldBalance;

        Exception exception = assertThrows(InsufficientFundsException.class,
                () -> validationService.validateAccountForSufficientBalance(type, sum, oldBalance));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("test validateFieldByPattern should contains pattern Violation")
    void testValidateFieldByPatternShouldContainsPatternViolation() {
        String field = "Юра!";
        String fieldName = "firstname";
        String patternString = "^[a-zA-Zа-яА-ЯёЁ]+$";
        Violation violation = new Violation(fieldName, "Field is out of pattern: " + patternString);
        List<Violation> violations = new ArrayList<>();

        validationService.validateFieldByPattern(field, fieldName, patternString, violations);

        assertThat(violations.get(0)).isEqualTo(violation);
    }

    @Test
    @DisplayName("test validateFieldByPattern should contains null Violation")
    void testValidateFieldByPatternShouldContainsNullViolation() {
        String fieldName = "firstname";
        String patternString = "^[a-zA-Zа-яА-ЯёЁ]+$";
        Violation violation = new Violation(fieldName, "Field can not be null");
        List<Violation> violations = new ArrayList<>();

        validationService.validateFieldByPattern(null, fieldName, patternString, violations);

        assertThat(violations.get(0)).isEqualTo(violation);
    }

    @Test
    @DisplayName("test validateRequestForNull should throw ValidationException with expected message")
    void testValidateRequestForNullShouldThrowValidationExceptionWithExpectedMessage() {
        String requestName = "name";
        String expectedMessage = "%s can not be null".formatted(requestName);

        doReturn(expectedMessage)
                .when(gson)
                .toJson(new ValidationResponse(List.of(new Violation(requestName, expectedMessage))));

        Exception exception = assertThrows(ValidationException.class,
                () -> validationService.validateRequestForNull(null, requestName, gson));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("test validateBigDecimalFieldForPositive should contains null Violation")
    void testValidateBigDecimalFieldForPositiveShouldContainsNullViolation() {
        String fieldName = "sum";
        Violation violation = new Violation(fieldName, "Field can not be null");
        List<Violation> violations = new ArrayList<>();

        validationService.validateBigDecimalFieldForPositive(null, fieldName, violations);

        assertThat(violations.get(0)).isEqualTo(violation);
    }

    @Test
    @DisplayName("test validateBigDecimalFieldForPositive should contains positive Violation")
    void testValidateBigDecimalFieldForPositiveShouldContainsPositiveViolation() {
        BigDecimal field = BigDecimal.ZERO;
        String fieldName = "sum";
        Violation violation = new Violation(fieldName, "Field must be greater than 0");
        List<Violation> violations = new ArrayList<>();

        validationService.validateBigDecimalFieldForPositive(field, fieldName, violations);

        assertThat(violations.get(0)).isEqualTo(violation);
    }

    @Test
    @DisplayName("test validateLongFieldForPositive should contains null Violation")
    void testValidateLongFieldForPositiveShouldContainsNullViolation() {
        String fieldName = "id";
        Violation violation = new Violation(fieldName, "Field can not be null");
        List<Violation> violations = new ArrayList<>();

        validationService.validateLongFieldForPositive(null, fieldName, violations);

        assertThat(violations.get(0)).isEqualTo(violation);
    }

    @Test
    @DisplayName("test validateLongFieldForPositive should contains positive Violation")
    void testValidateLongFieldForPositiveShouldContainsPositiveViolation() {
        long field = -1L;
        String fieldName = "id";
        Violation violation = new Violation(fieldName, "Field must be greater than 0");
        List<Violation> violations = new ArrayList<>();

        validationService.validateLongFieldForPositive(field, fieldName, violations);

        assertThat(violations.get(0)).isEqualTo(violation);
    }

}
