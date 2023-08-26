package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.cleverbank.dto.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.TransferBalanceResponse;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.service.TransactionService;
import ru.clevertec.cleverbank.service.impl.TransactionServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/transactions")
public class TransactionServlet extends HttpServlet {

    private final transient TransactionService transactionService = new TransactionServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        Gson gson = new Gson();
        String transactionJson;
        JsonObject jsonObject = gson.fromJson(result.toString(), JsonObject.class);
        if (jsonObject.has("type")) {
            ChangeBalanceRequest request = gson.fromJson(jsonObject.toString(), ChangeBalanceRequest.class);
            ChangeBalanceResponse response = transactionService.changeBalance(request);
            transactionJson = gson.toJson(response);
        } else {
            TransferBalanceRequest request = gson.fromJson(jsonObject.toString(), TransferBalanceRequest.class);
            TransferBalanceResponse response;
            try {
                response = transactionService.transferBalance(request);
            } catch (SQLException e) {
                throw new JDBCConnectionException();
            }
            transactionJson = gson.toJson(response);
        }

        resp.setStatus(201);
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(transactionJson);
        printWriter.flush();
    }

}
