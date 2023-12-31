package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.service.BankService;
import ru.clevertec.cleverbank.service.impl.BankServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@AllArgsConstructor
@WebServlet(urlPatterns = "/banks")
public class BankServlet extends HttpServlet {

    private final transient BankService bankService;
    private final transient Gson gson;

    public BankServlet() {
        bankService = new BankServiceImpl();
        gson = new Gson();
    }

    /**
     * Переопределяет метод doGet, который обрабатывает GET-запросы к ресурсу /banks.
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
     * Переопределяет метод doPost, который обрабатывает POST-запросы к ресурсу /banks.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BankRequest request = (BankRequest) req.getAttribute("bankRequest");
        BankResponse response = bankService.save(request);
        String bankJson = gson.toJson(response);

        PrintWriter printWriter = resp.getWriter();
        resp.setStatus(201);
        printWriter.print(bankJson);
        printWriter.flush();
    }

    /**
     * Переопределяет метод doPut, который обрабатывает PUT-запросы к ресурсу /banks.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        BankRequest request = (BankRequest) req.getAttribute("bankRequest");
        BankResponse response = bankService.update(Long.valueOf(id), request);
        String bankJson = gson.toJson(response);

        PrintWriter printWriter = resp.getWriter();
        resp.setStatus(201);
        printWriter.print(bankJson);
        printWriter.flush();
    }

    /**
     * Переопределяет метод doDelete, который обрабатывает DELETE-запросы к ресурсу /banks.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        PrintWriter printWriter = resp.getWriter();
        DeleteResponse response = bankService.delete(Long.valueOf(id));
        String deleteJson = gson.toJson(response);
        printWriter.print(deleteJson);
        printWriter.flush();
    }

    /**
     * Метод findById, который находит банк по его id и выводит его в формате JSON.
     *
     * @param id          String, представляющая id банка
     * @param printWriter объект PrintWriter, представляющий поток печати для вывода данных о банке
     */
    private void findById(String id, PrintWriter printWriter) {
        BankResponse response = bankService.findByIdResponse(Long.valueOf(id));
        String bankJson = gson.toJson(response);
        printWriter.print(bankJson);
        printWriter.flush();
    }

    /**
     * Метод findAll, который получает все банки и выводит их в формате JSON.
     *
     * @param printWriter объект PrintWriter, представляющий поток печати для вывода данных о всех банках
     */
    private void findAll(PrintWriter printWriter) {
        List<BankResponse> responses = bankService.findAll();
        String bankJson = gson.toJson(responses);
        printWriter.print(bankJson);
        printWriter.flush();
    }

}
