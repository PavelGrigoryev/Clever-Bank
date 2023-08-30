package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.service.TransactionService;
import ru.clevertec.cleverbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;

@Slf4j
@WebServlet(urlPatterns = "/transactions", asyncSupported = true)
public class TransactionServlet extends HttpServlet {

    private final transient TransactionService transactionService = new TransactionServiceImpl();
    private final transient Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        AsyncContext asyncContext = req.startAsync();
        CompletableFuture.runAsync(() -> {
            try {
                String transactionJson;
                ChangeBalanceRequest changeRequest = (ChangeBalanceRequest) asyncContext.getRequest()
                        .getAttribute("changeBalanceRequest");
                TransferBalanceRequest transferRequest = (TransferBalanceRequest) asyncContext.getRequest()
                        .getAttribute("transferBalanceRequest");
                if (changeRequest != null) {
                    transactionJson = changeBalance(gson, changeRequest);
                } else if (transferRequest != null) {
                    transactionJson = transferBalance(gson, transferRequest);
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        String senderAccountId = req.getParameter("sender_account_id");
        String recipientAccountId = req.getParameter("recipient_account_id");
        PrintWriter printWriter = resp.getWriter();
        if (id != null) {
            findById(id, printWriter);
        } else if (senderAccountId != null) {
            findAllBySendersAccountId(senderAccountId, printWriter);
        } else {
            findAllByRecipientAccountId(recipientAccountId, printWriter);
        }
    }

    private String changeBalance(Gson gson, ChangeBalanceRequest request) {
        ChangeBalanceResponse response = transactionService.changeBalance(request);
        return gson.toJson(response);
    }

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

    private String findAllByPeriodOfDateAndAccountId(Gson gson, TransactionStatementRequest request) {
        TransactionStatementResponse response = transactionService.findAllByPeriodOfDateAndAccountId(request);
        return gson.toJson(response);
    }

    private void findById(String id, PrintWriter printWriter) {
        TransactionResponse response = transactionService.findById(Long.valueOf(id));
        String transactionJson = gson.toJson(response);
        printWriter.print(transactionJson);
        printWriter.flush();
    }

    private void findAllBySendersAccountId(String id, PrintWriter printWriter) {
        List<TransactionResponse> responses = transactionService.findAllBySendersAccountId(id);
        String transactionJson = gson.toJson(responses);
        printWriter.print(transactionJson);
        printWriter.flush();
    }

    private void findAllByRecipientAccountId(String id, PrintWriter printWriter) {
        List<TransactionResponse> responses = transactionService.findAllByRecipientAccountId(id);
        String transactionJson = gson.toJson(responses);
        printWriter.print(transactionJson);
        printWriter.flush();
    }

}
