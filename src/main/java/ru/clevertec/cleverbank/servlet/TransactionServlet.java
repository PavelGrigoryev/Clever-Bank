package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dto.transaction.AmountStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.ExchangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.TransactionService;
import ru.clevertec.cleverbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;

@Slf4j
@AllArgsConstructor
@WebServlet(urlPatterns = "/transactions", asyncSupported = true)
public class TransactionServlet extends HttpServlet {

    private final transient TransactionService transactionService;
    private final transient Gson gson;

    public TransactionServlet() {
        transactionService = new TransactionServiceImpl();
        gson = new Gson();
    }

    /**
     * Переопределяет метод doPost, который обрабатывает POST-запросы к ресурсу /transactions.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        AsyncContext asyncContext = req.startAsync();
        CompletableFuture.runAsync(() -> {
            try {
                String transactionJson;
                ChangeBalanceRequest changeRequest = (ChangeBalanceRequest) asyncContext.getRequest()
                        .getAttribute("changeBalanceRequest");
                if (changeRequest != null) {
                    transactionJson = changeBalance(gson, changeRequest);
                } else {
                    TransactionStatementRequest statementRequest = (TransactionStatementRequest) asyncContext.getRequest()
                            .getAttribute("statementRequest");
                    transactionJson = findAllByPeriodOfDateAndAccountId(gson, statementRequest);
                }

                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.setStatus(201);
                PrintWriter printWriter = response.getWriter();
                printWriter.print(transactionJson);
                printWriter.flush();
            } catch (Exception e) {
                req.setAttribute(ERROR_EXCEPTION, e);
                asyncContext.dispatch("/exception_handler");
            } finally {
                asyncContext.complete();
            }
        });
    }

    /**
     * Переопределяет метод doPut, который обрабатывает PUT-запросы к ресурсу /transactions.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        AsyncContext asyncContext = req.startAsync();
        CompletableFuture.runAsync(() -> {
            try {
                String transactionJson;
                TransferBalanceRequest transferRequest = (TransferBalanceRequest) asyncContext.getRequest()
                        .getAttribute("transferBalanceRequest");
                if (transferRequest != null) {
                    if (transferRequest.type() == Type.EXCHANGE) {
                        transactionJson = exchangeBalance(gson, transferRequest);
                    } else {
                        transactionJson = transferBalance(gson, transferRequest);
                    }
                } else {
                    TransactionStatementRequest statementRequest = (TransactionStatementRequest) asyncContext.getRequest()
                            .getAttribute("amountRequest");
                    transactionJson = findSumOfFundsByPeriodOfDateAndAccountId(gson, statementRequest);
                }
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.setStatus(201);
                PrintWriter printWriter = response.getWriter();
                printWriter.print(transactionJson);
                printWriter.flush();
            } catch (Exception e) {
                req.setAttribute(ERROR_EXCEPTION, e);
                asyncContext.dispatch("/exception_handler");
            } finally {
                asyncContext.complete();
            }
        });
    }

    /**
     * Переопределяет метод doGet, который обрабатывает GET-запросы к ресурсу /transactions.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        String senderAccountId = req.getParameter("account_sender_id");
        String recipientAccountId = req.getParameter("account_recipient_id");
        PrintWriter printWriter = resp.getWriter();
        if (id != null) {
            findById(id, printWriter);
        } else if (senderAccountId != null) {
            findAllBySendersAccountId(senderAccountId, printWriter);
        } else {
            findAllByRecipientAccountId(recipientAccountId, printWriter);
        }
    }

    /**
     * Метод changeBalance, который выполняет операцию изменения баланса счёта и возвращает строку JSON с данными о транзакции.
     *
     * @param gson    объект Gson, представляющий парсер JSON
     * @param request объект ChangeBalanceRequest, представляющий запрос на изменение баланса счета
     * @return String JSON, представляющая ответ с данными о транзакции
     */
    private String changeBalance(Gson gson, ChangeBalanceRequest request) {
        ChangeBalanceResponse response = transactionService.changeBalance(request);
        return gson.toJson(response);
    }

    /**
     * Метод transferBalance, который выполняет операцию перевода средств между счетами и возвращает строку JSON
     * с данными о транзакции.
     *
     * @param gson    объект Gson, представляющий парсер JSON
     * @param request объект TransferBalanceRequest, представляющий запрос на перевод средств между счетами
     * @return String JSON, представляющая ответ с данными о транзакции
     */
    private String transferBalance(Gson gson, TransferBalanceRequest request) {
        TransferBalanceResponse response;
        try {
            response = transactionService.transferBalance(request);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return gson.toJson(response);
    }

    /**
     * Метод exchangeBalance, который выполняет операцию обмена валют средств между счетами и возвращает строку JSON
     * с данными о транзакции.
     *
     * @param gson    объект Gson, представляющий парсер JSON
     * @param request объект TransferBalanceRequest, представляющий запрос на перевод средств между счетами
     * @return String JSON, представляющая ответ с данными о транзакции
     */
    private String exchangeBalance(Gson gson, TransferBalanceRequest request) {
        ExchangeBalanceResponse response;
        try {
            response = transactionService.exchangeBalance(request);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return gson.toJson(response);
    }

    /**
     * Метод findAllByPeriodOfDateAndAccountId, который получает выписку по транзакциям по счёту за определенный период
     * дат и возвращает строку JSON с данными о транзакциях.
     *
     * @param gson    объект Gson, представляющий парсер JSON
     * @param request объект TransactionStatementRequest, представляющий запрос на получение выписки по транзакциям по
     *                счету за период дат
     * @return String JSON, представляющая ответ с данными о транзакциях
     */
    private String findAllByPeriodOfDateAndAccountId(Gson gson, TransactionStatementRequest request) {
        TransactionStatementResponse response = transactionService.findAllByPeriodOfDateAndAccountId(request);
        return gson.toJson(response);
    }

    /**
     * Метод findSumOfFundsByPeriodOfDateAndAccountId, который получает сумму потраченных и полученных средств по счёту
     * за определенный период дат и возвращает строку JSON с данными о суммах.
     *
     * @param gson    объект Gson, представляющий парсер JSON
     * @param request объект TransactionStatementRequest, представляющий запрос на получение суммы потраченных и
     *                полученных средств по счёту за определенный период дат
     * @return String JSON, представляющая ответ с данными о суммах
     */
    private String findSumOfFundsByPeriodOfDateAndAccountId(Gson gson, TransactionStatementRequest request) {
        AmountStatementResponse response = transactionService.findSumOfFundsByPeriodOfDateAndAccountId(request);
        return gson.toJson(response);
    }

    /**
     * Метод findById, который находит транзакцию по ее id и выводит ее в формате JSON.
     *
     * @param id          String, представляющая id транзакции
     * @param printWriter объект PrintWriter, представляющий поток печати для вывода данных о транзакции
     */
    private void findById(String id, PrintWriter printWriter) {
        TransactionResponse response = transactionService.findById(Long.valueOf(id));
        String transactionJson = gson.toJson(response);
        printWriter.print(transactionJson);
        printWriter.flush();
    }

    /**
     * Метод findAllBySendersAccountId, который получает все транзакции по счёту отправителя и выводит их в формате JSON.
     *
     * @param id          String, представляющая id счета отправителя
     * @param printWriter объект PrintWriter, представляющий поток печати для вывода данных о транзакциях
     */
    private void findAllBySendersAccountId(String id, PrintWriter printWriter) {
        List<TransactionResponse> responses = transactionService.findAllBySendersAccountId(id);
        String transactionJson = gson.toJson(responses);
        printWriter.print(transactionJson);
        printWriter.flush();
    }

    /**
     * Метод findAllByRecipientAccountId, который получает все транзакции по счёту получателя и выводит их в формате JSON.
     *
     * @param id          String, представляющая id счета получателя
     * @param printWriter объект PrintWriter, представляющий поток печати для вывода данных о транзакциях
     */
    private void findAllByRecipientAccountId(String id, PrintWriter printWriter) {
        List<TransactionResponse> responses = transactionService.findAllByRecipientAccountId(id);
        String transactionJson = gson.toJson(responses);
        printWriter.print(transactionJson);
        printWriter.flush();
    }

}
