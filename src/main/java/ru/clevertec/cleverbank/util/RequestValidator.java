package ru.clevertec.cleverbank.util;

import lombok.experimental.UtilityClass;
import ru.clevertec.cleverbank.exception.handler.Violation;

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

}
