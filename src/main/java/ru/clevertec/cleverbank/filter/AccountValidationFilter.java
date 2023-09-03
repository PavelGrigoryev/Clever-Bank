package ru.clevertec.cleverbank.filter;

import com.google.gson.Gson;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import ru.clevertec.cleverbank.aspect.annotation.ExceptionLoggable;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;
import ru.clevertec.cleverbank.exception.handler.ValidationResponse;
import ru.clevertec.cleverbank.exception.handler.Violation;
import ru.clevertec.cleverbank.service.ValidationService;
import ru.clevertec.cleverbank.service.impl.ValidationServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebFilter(urlPatterns = "/accounts")
public class AccountValidationFilter implements Filter {

    private final Gson gson = new Gson();
    private final ValidationService validationService = new ValidationServiceImpl();

    /**
     * Переопределяет метод doFilter, чтобы проверить корректность данных в запросе на создание счёта.
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
            validateAccountRequest(req);
        }
        chain.doFilter(request, response);
    }

    /**
     * Валидирует данные в запросе на создание счета и устанавливает атрибут "accountRequest" с объектом AccountRequest
     * в запросе.
     *
     * @param req объект HttpServletRequest, содержащий данные запроса
     * @throws IOException если произошла ошибка ввода-вывода
     */
    private void validateAccountRequest(HttpServletRequest req) throws IOException {
        AccountRequest request = gson.fromJson(extractJsonFromBody(req), AccountRequest.class);
        List<Violation> violations = new ArrayList<>();

        validationService.validateRequestForNull(request, "Account", gson);

        if (request.currency() == null) {
            Violation violation = new Violation("currency", "Available currencies are: BYN, RUB, USD or EUR");
            violations.add(violation);
        }

        validationService.validateBigDecimalFieldForPositive(request.balance(), "balance", violations);
        validationService.validateLongFieldForPositive(request.bankId(), "bank_id", violations);
        validationService.validateLongFieldForPositive(request.userId(), "user_id", violations);

        if (!violations.isEmpty()) {
            String validationJson = gson.toJson(new ValidationResponse(violations));
            throw new ValidationException(validationJson);
        }

        req.setAttribute("accountRequest", request);
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
