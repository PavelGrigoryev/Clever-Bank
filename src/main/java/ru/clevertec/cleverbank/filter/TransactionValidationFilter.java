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
import ru.clevertec.cleverbank.aspect.annotation.ExceptionLoggable;
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

    /**
     * Переопределяет метод doFilter, чтобы проверить корректность данных в запросе на выполнение транзакции.
     *
     * @param request  объект ServletRequest, содержащий данные запроса
     * @param response объект ServletResponse, содержащий данные ответа
     * @param chain    объект FilterChain, который позволяет передать запрос и ответ дальше по цепочке фильтров
     * @throws IOException      если произошла ошибка ввода-вывода
     * @throws ServletException если произошла ошибка сервлета
     */
    @Override
    @ExceptionLoggable
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            JsonObject jsonObject = gson.fromJson(extractJsonFromBody(req), JsonObject.class);
            validationService.validateRequestForNull(jsonObject, "Transaction", gson);
            if (jsonObject.has("type")) {
                validateChangeBalanceRequest(req, jsonObject);
            } else {
                validateStatementRequest(req, jsonObject);
            }
        } else if ("PUT".equalsIgnoreCase(req.getMethod())) {
            JsonObject jsonObject = gson.fromJson(extractJsonFromBody(req), JsonObject.class);
            validationService.validateRequestForNull(jsonObject, "Transaction", gson);
            if (jsonObject.has("type")) {
                validateTransferBalanceRequest(req, jsonObject);
            } else {
                validateAmountRequest(req, jsonObject);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Валидирует данные в запросе на изменение баланса счета и устанавливает атрибут "changeBalanceRequest" с объектом
     * ChangeBalanceRequest в запросе.
     *
     * @param req        объект HttpServletRequest, содержащий данные запроса
     * @param jsonObject объект JsonObject, содержащий JSON-данные из тела запроса
     */
    private void validateChangeBalanceRequest(HttpServletRequest req, JsonObject jsonObject) {
        ChangeBalanceRequest request = gson.fromJson(jsonObject.toString(), ChangeBalanceRequest.class);
        List<Violation> violations = new ArrayList<>();

        validationService.validateAccountId(request.accountRecipientId(), "account_recipient_id", violations);
        validationService.validateAccountId(request.accountSenderId(), "account_sender_id", violations);
        if (request.type() == null || request.type() == Type.TRANSFER || request.type() == Type.EXCHANGE) {
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

    /**
     * Валидирует данные в запросе на перевод средств между счетами и устанавливает атрибут "transferBalanceRequest"
     * с объектом TransferBalanceRequest в запросе.
     *
     * @param req        объект HttpServletRequest, содержащий данные запроса
     * @param jsonObject объект JsonObject, содержащий JSON-данные из тела запроса
     */
    private void validateTransferBalanceRequest(HttpServletRequest req, JsonObject jsonObject) {
        TransferBalanceRequest request = gson.fromJson(jsonObject.toString(), TransferBalanceRequest.class);
        List<Violation> violations = new ArrayList<>();

        validationService.validateAccountId(request.accountRecipientId(), "account_recipient_id", violations);
        validationService.validateAccountId(request.accountSenderId(), "account_sender_id", violations);
        if (request.type() == null || request.type() == Type.REPLENISHMENT || request.type() == Type.WITHDRAWAL) {
            Violation violation = new Violation("type", "Available types are: TRANSFER or EXCHANGE");
            violations.add(violation);
        }
        validationService.validateBigDecimalFieldForPositive(request.sum(), "sum", violations);

        if (!violations.isEmpty()) {
            String validationJson = gson.toJson(new ValidationResponse(violations));
            throw new ValidationException(validationJson);
        }

        req.setAttribute("transferBalanceRequest", request);
    }

    /**
     * Валидирует данные в запросе на получение выписки по счету и устанавливает атрибут "statementRequest" с объектом
     * TransactionStatementRequest в запросе.
     *
     * @param req        объект HttpServletRequest, содержащий данные запроса
     * @param jsonObject объект JsonObject, содержащий JSON-данные из тела запроса
     */
    private void validateStatementRequest(HttpServletRequest req, JsonObject jsonObject) {
        TransactionStatementRequest request = validateTransactionStatementRequest(jsonObject);
        req.setAttribute("statementRequest", request);
    }

    /**
     * Валидирует данные в запросе на изменение суммы транзакции и устанавливает атрибут "amountRequest" с объектом
     * TransactionStatementRequest в запросе.
     *
     * @param req        объект HttpServletRequest, содержащий данные запроса
     * @param jsonObject объект JsonObject, содержащий JSON-данные из тела запроса
     */
    private void validateAmountRequest(HttpServletRequest req, JsonObject jsonObject) {
        TransactionStatementRequest request = validateTransactionStatementRequest(jsonObject);
        req.setAttribute("amountRequest", request);
    }

    /**
     * Валидирует данные в запросе на изменение суммы транзакции.
     *
     * @param jsonObject объект JsonObject, содержащий JSON-данные из тела запроса
     * @return валидный TransactionStatementRequest
     */
    private TransactionStatementRequest validateTransactionStatementRequest(JsonObject jsonObject) {
        TransactionStatementRequest request = gson.fromJson(jsonObject.toString(), TransactionStatementRequest.class);
        List<Violation> violations = new ArrayList<>();

        validationService.validateAccountId(request.accountId(), "account_id", violations);
        if (!violations.isEmpty()) {
            String validationJson = gson.toJson(new ValidationResponse(violations));
            throw new ValidationException(validationJson);
        }
        return request;
    }

    /**
     * Извлекает JSON-данные из тела запроса и возвращает их в виде строки.
     *
     * @param req объект HttpServletRequest, содержащий данные запроса
     * @return строка с JSON-данными из тела запроса
     * @throws IOException если произошла ошибка ввода-вывода
     */
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
