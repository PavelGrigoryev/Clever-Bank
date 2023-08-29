package ru.clevertec.cleverbank.filter;

import com.google.gson.Gson;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;
import ru.clevertec.cleverbank.exception.handler.ValidationResponse;
import ru.clevertec.cleverbank.exception.handler.Violation;
import ru.clevertec.cleverbank.service.ValidationService;
import ru.clevertec.cleverbank.service.impl.ValidationServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebFilter(urlPatterns = "/users")
public class UserValidationFilter implements Filter {

    private final Gson gson = new Gson();
    private final ValidationService validationService = new ValidationServiceImpl();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if ("POST".equalsIgnoreCase(req.getMethod()) || "PUT".equalsIgnoreCase(req.getMethod())) {
            validateUserRequest(req);
        }
        chain.doFilter(request, response);
    }

    private void validateUserRequest(HttpServletRequest req) throws IOException {
        UserRequest request = gson.fromJson(extractJsonFromBody(req), UserRequest.class);
        List<Violation> violations = new ArrayList<>();

        validationService.validateRequestForNull(request, "User", gson);
        validationService.validateFieldByPattern(request.lastname(), "lastname",
                "^[a-zA-Zа-яА-ЯёЁ]+$", violations);
        validationService.validateFieldByPattern(request.firstname(), "firstname",
                "^[a-zA-Zа-яА-ЯёЁ]+$", violations);
        validationService.validateFieldByPattern(request.surname(), "surname",
                "^[a-zA-Zа-яА-ЯёЁ]+$", violations);
        validationService.validateFieldByPattern(request.mobileNumber(), "mobile_number",
                "^\\+\\d{1,3} \\(\\d{1,3}\\) \\d{3}-\\d{2}-\\d{2}$", violations);

        if (!violations.isEmpty()) {
            String validationJson = gson.toJson(new ValidationResponse(violations));
            throw new ValidationException(validationJson);
        }

        req.setAttribute("userRequest", request);
    }

    private String extractJsonFromBody(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

}
