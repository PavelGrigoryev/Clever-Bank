package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.cleverbank.dto.BalanceChangeRequest;
import ru.clevertec.cleverbank.dto.BalanceChangeResponse;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.impl.AccountServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/accounts")
public class AccountServlet extends HttpServlet {

    private final transient AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        BalanceChangeRequest request = new Gson().fromJson(result.toString(), BalanceChangeRequest.class);
        BalanceChangeResponse response = accountService.replenish(request);
        String transactionJson = new Gson().toJson(response);

        resp.setStatus(201);
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(transactionJson);
        printWriter.flush();
    }

}