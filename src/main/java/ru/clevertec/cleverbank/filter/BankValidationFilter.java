package ru.clevertec.cleverbank.filter;

import com.google.gson.Gson;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;
import ru.clevertec.cleverbank.exception.handler.ValidationResponse;
import ru.clevertec.cleverbank.exception.handler.Violation;
import ru.clevertec.cleverbank.util.RequestValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebFilter(urlPatterns = "/banks")
public class BankValidationFilter implements Filter {

    private final Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if ("POST".equalsIgnoreCase(req.getMethod()) || "PUT".equalsIgnoreCase(req.getMethod())) {
            validateBankRequest(req);
        }
        chain.doFilter(request, response);
    }

    private void validateBankRequest(HttpServletRequest req) throws IOException {
        BankRequest request = gson.fromJson(extractJsonFromBody(req), BankRequest.class);
        List<Violation> violations = new ArrayList<>();

        RequestValidator.validateRequestForNull(request, "Bank", gson);
        RequestValidator.validateFieldByPattern(request.name(), "name",
                "^[a-zA-Zа-яА-ЯёЁ @_-]+$", violations);
        RequestValidator.validateFieldByPattern(request.address(), "address",
                "^[a-zA-Zа-яА-ЯёЁ0-9 .,-]+$", violations);
        RequestValidator.validateFieldByPattern(request.phoneNumber(), "phone_number",
                "^\\+\\d{1,3} \\(\\d{1,3}\\) \\d{3}-\\d{2}-\\d{2}$", violations);

        if (!violations.isEmpty()) {
            String validationJson = gson.toJson(new ValidationResponse(violations));
            throw new ValidationException(validationJson);
        }

        req.setAttribute("bankRequest", request);
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
