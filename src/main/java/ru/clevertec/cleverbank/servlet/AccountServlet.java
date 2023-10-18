package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.impl.AccountServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@AllArgsConstructor
@WebServlet(urlPatterns = "/accounts")
public class AccountServlet extends HttpServlet {

    private final transient AccountService accountService;
    private final transient Gson gson;

    public AccountServlet() {
        accountService = new AccountServiceImpl();
        gson = new Gson();
    }

    /**
     * Переопределяет метод doGet, который обрабатывает GET-запросы к ресурсу /accounts.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        PrintWriter printWriter = resp.getWriter();
        if (id != null) {
            findById(id, printWriter);
        } else {
            findAll(printWriter);
        }
    }

    /**
     * Переопределяет метод doPost, который обрабатывает POST-запросы к ресурсу /accounts.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AccountRequest request = (AccountRequest) req.getAttribute("accountRequest");
        AccountResponse response = accountService.save(request);
        String accountJson = gson.toJson(response);

        PrintWriter printWriter = resp.getWriter();
        resp.setStatus(201);
        printWriter.print(accountJson);
        printWriter.flush();
    }

    /**
     * Переопределяет метод doPut, который обрабатывает PUT-запросы к ресурсу /accounts.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        PrintWriter printWriter = resp.getWriter();
        AccountResponse response = accountService.closeAccount(id);
        String accountJson = gson.toJson(response);
        resp.setStatus(201);
        printWriter.print(accountJson);
        printWriter.flush();
    }

    /**
     * Переопределяет метод doDelete, который обрабатывает DELETE-запросы к ресурсу /accounts.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        PrintWriter printWriter = resp.getWriter();
        DeleteResponse response = accountService.delete(id);
        String deleteJson = gson.toJson(response);
        printWriter.print(deleteJson);
        printWriter.flush();
    }

    /**
     * Метод findById, который находит счёт по его id и выводит его в формате JSON.
     *
     * @param id          String, представляющая id счета
     * @param printWriter объект PrintWriter, представляющий поток печати для вывода данных о счёте
     */
    private void findById(String id, PrintWriter printWriter) {
        AccountResponse response = accountService.findByIdResponse(id);
        String accountJson = gson.toJson(response);
        printWriter.print(accountJson);
        printWriter.flush();
    }

    /**
     * Приватный метод findAll, который получает все счета и выводит их в формате JSON.
     *
     * @param printWriter объект PrintWriter, представляющий поток печати для вывода данных о всех счетах
     */
    private void findAll(PrintWriter printWriter) {
        List<AccountResponse> responses = accountService.findAllResponses();
        String accountsJson = gson.toJson(responses);
        printWriter.print(accountsJson);
        printWriter.flush();
    }

}
