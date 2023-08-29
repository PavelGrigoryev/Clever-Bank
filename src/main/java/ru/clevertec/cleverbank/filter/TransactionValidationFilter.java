package ru.clevertec.cleverbank.filter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceRequest;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;
import ru.clevertec.cleverbank.exception.handler.ValidationResponse;
import ru.clevertec.cleverbank.exception.handler.Violation;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.ValidationService;
import ru.clevertec.cleverbank.service.impl.ValidationServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebFilter(urlPatterns = "/transactions", asyncSupported = true)
public class TransactionValidationFilter implements Filter {

    private final Gson gson = new Gson();
    private final ValidationService validationService = new ValidationServiceImpl();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            JsonObject jsonObject = gson.fromJson(extractJsonFromBody(req), JsonObject.class);

            validationService.validateRequestForNull(jsonObject, "Transaction", gson);

            if (jsonObject.has("type")) {
                validateChangeBalanceRequest(req, jsonObject);
            } else if (jsonObject.has("sender_account_id")) {
                validateTransferBalanceRequest(req, jsonObject);
            } else {
                validateStatementRequest(req, jsonObject);
            }
        }
        chain.doFilter(request, response);
    }

    private void validateChangeBalanceRequest(HttpServletRequest req, JsonObject jsonObject) {
        ChangeBalanceRequest request = gson.fromJson(jsonObject.toString(), ChangeBalanceRequest.class);
        List<Violation> violations = new ArrayList<>();

        if (request.type() == null || request.type() == Type.TRANSFER) {
            Violation violation = new Violation("type", "Available types are: REPLENISHMENT or WITHDRAWAL");
            violations.add(violation);
        }

        validationService.validateBigDecimalFieldForPositive(request.sum(), "sum", violations);

        if (!violations.isEmpty()) {
            String validationJson = gson.toJson(new ValidationResponse(violations));
            throw new ValidationException(validationJson);
        }

        req.setAttribute("changeBalanceRequest", request);
    }

    private void validateTransferBalanceRequest(HttpServletRequest req, JsonObject jsonObject) {
        TransferBalanceRequest request = gson.fromJson(jsonObject.toString(), TransferBalanceRequest.class);
        List<Violation> violations = new ArrayList<>();

        validationService.validateBigDecimalFieldForPositive(request.sum(), "sum", violations);

        if (!violations.isEmpty()) {
            String validationJson = gson.toJson(new ValidationResponse(violations));
            throw new ValidationException(validationJson);
        }

        req.setAttribute("transferBalanceRequest", request);
    }

    private void validateStatementRequest(HttpServletRequest req, JsonObject jsonObject) {
        TransactionStatementRequest request = gson.fromJson(jsonObject.toString(), TransactionStatementRequest.class);
        //todo
        req.setAttribute("statementRequest", request);
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
