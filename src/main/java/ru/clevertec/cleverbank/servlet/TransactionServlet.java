package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.cleverbank.dto.TransactionRequest;
import ru.clevertec.cleverbank.dto.TransactionResponse;
import ru.clevertec.cleverbank.service.TransactionService;
import ru.clevertec.cleverbank.service.impl.TransactionServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/accounts")
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

        TransactionRequest request = new Gson().fromJson(result.toString(), TransactionRequest.class);
        TransactionResponse response = transactionService.changeBalance(request);
        String transactionJson = new Gson().toJson(response);

        resp.setStatus(201);
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(transactionJson);
        printWriter.flush();
    }

}
