package ru.clevertec.cleverbank.servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.service.UserService;
import ru.clevertec.cleverbank.service.impl.UserServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/users")
public class UserServlet extends HttpServlet {

    private final transient UserService userService = new UserServiceImpl();
    private final transient Gson gson = new Gson();

    /**
     * Переопределяет метод doGet, который обрабатывает GET-запросы к ресурсу /users.
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
     * Переопределяет метод doPost, который обрабатывает POST-запросы к ресурсу /users.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserRequest request = (UserRequest) req.getAttribute("userRequest");
        UserResponse response = userService.save(request);
        String userJson = gson.toJson(response);

        PrintWriter printWriter = resp.getWriter();
        resp.setStatus(201);
        printWriter.print(userJson);
        printWriter.flush();
    }

    /**
     * Переопределяет метод doPut, который обрабатывает PUT-запросы к ресурсу /users.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        UserRequest request = (UserRequest) req.getAttribute("userRequest");
        UserResponse response = userService.update(Long.valueOf(id), request);
        String userJson = gson.toJson(response);

        PrintWriter printWriter = resp.getWriter();
        resp.setStatus(201);
        printWriter.print(userJson);
        printWriter.flush();
    }

    /**
     * Переопределяет метод doDelete, который обрабатывает DELETE-запросы к ресурсу /users.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        PrintWriter printWriter = resp.getWriter();
        DeleteResponse response = userService.delete(Long.valueOf(id));
        String deleteJson = gson.toJson(response);
        printWriter.print(deleteJson);
        printWriter.flush();
    }

    /**
     * Метод findById, который находит пользователя по его id и выводит его в формате JSON.
     *
     * @param id          String, представляющая id пользователя
     * @param printWriter объект PrintWriter, представляющий поток печати для вывода данных о пользователе
     */
    private void findById(String id, PrintWriter printWriter) {
        UserResponse response = userService.findByIdResponse(Long.valueOf(id));
        String userJson = gson.toJson(response);
        printWriter.print(userJson);
        printWriter.flush();
    }

    /**
     * Метод findAll, который получает всех пользователей и выводит их в формате JSON.
     *
     * @param printWriter объект PrintWriter, представляющий поток печати для вывода данных о всех пользователях
     */
    private void findAll(PrintWriter printWriter) {
        List<UserResponse> responses = userService.findAll();
        String userJson = gson.toJson(responses);
        printWriter.print(userJson);
        printWriter.flush();
    }

}
