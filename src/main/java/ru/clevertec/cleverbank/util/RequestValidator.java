package ru.clevertec.cleverbank.util;

import com.google.gson.Gson;
import lombok.experimental.UtilityClass;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;
import ru.clevertec.cleverbank.exception.handler.ValidationResponse;
import ru.clevertec.cleverbank.exception.handler.Violation;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class RequestValidator {

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

    public void validateRequestForNull(Object object, String requestName, Gson gson) {
        if (object == null) {
            Violation violation = new Violation(requestName, "%s can not be null".formatted(requestName));
            String validationJson = gson.toJson(new ValidationResponse(List.of(violation)));
            throw new ValidationException(validationJson);
        }
    }

    public void validateBigDecimalFieldForPositive(BigDecimal field, String fieldName, List<Violation> violations) {
        if (field == null) {
            Violation violation = new Violation(fieldName, "Field can not be null");
            violations.add(violation);
        } else if (field.compareTo(BigDecimal.ZERO) <= 0) {
            Violation violation = new Violation(fieldName, "Field must be grater than 0");
            violations.add(violation);
        }
    }

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
