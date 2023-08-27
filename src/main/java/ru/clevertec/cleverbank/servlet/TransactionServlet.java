package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dto.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.TransferBalanceResponse;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.service.TransactionService;
import ru.clevertec.cleverbank.service.impl.TransactionServiceImpl;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;

@Slf4j
@WebServlet(urlPatterns = "/transactions", asyncSupported = true)
public class TransactionServlet extends HttpServlet {

    private final transient TransactionService transactionService = new TransactionServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        AsyncContext asyncContext = req.startAsync();
        CompletableFuture.runAsync(() -> {
            try {
                BufferedReader reader = asyncContext.getRequest().getReader();
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Gson gson = new Gson();
                String transactionJson;
                JsonObject jsonObject = gson.fromJson(result.toString(), JsonObject.class);
                if (jsonObject.has("type")) {
                    transactionJson = changeBalance(gson, jsonObject);
                } else {
                    transactionJson = transferBalance(gson, jsonObject);
                }

                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.setStatus(201);
                PrintWriter printWriter = response.getWriter();
                printWriter.print(transactionJson);
                printWriter.flush();

                asyncContext.complete();
            } catch (Exception e) {
                req.setAttribute(ERROR_EXCEPTION, e);
                asyncContext.dispatch("/exception_handler");
                asyncContext.complete();
            }
        });
    }

    private String changeBalance(Gson gson, JsonObject jsonObject) {
        ChangeBalanceRequest request = gson.fromJson(jsonObject.toString(), ChangeBalanceRequest.class);
        ChangeBalanceResponse response = transactionService.changeBalance(request);
        return gson.toJson(response);
    }

    private String transferBalance(Gson gson, JsonObject jsonObject) {
        TransferBalanceRequest request = gson.fromJson(jsonObject.toString(), TransferBalanceRequest.class);
        TransferBalanceResponse response;
        try {
            response = transactionService.transferBalance(request);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return gson.toJson(response);
    }

}
