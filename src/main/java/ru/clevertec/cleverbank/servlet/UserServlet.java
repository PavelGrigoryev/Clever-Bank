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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/users")
public class UserServlet extends HttpServlet {

    private final transient UserService userService = new UserServiceImpl();
    private final transient Gson gson = new Gson();

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserRequest request = gson.fromJson(extractJsonFromBody(req), UserRequest.class);
        UserResponse response = userService.save(request);
        String userJson = gson.toJson(response);

        PrintWriter printWriter = resp.getWriter();
        resp.setStatus(201);
        printWriter.print(userJson);
        printWriter.flush();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        UserRequest request = gson.fromJson(extractJsonFromBody(req), UserRequest.class);
        UserResponse response = userService.update(Long.valueOf(id), request);
        String userJson = gson.toJson(response);

        PrintWriter printWriter = resp.getWriter();
        resp.setStatus(201);
        printWriter.print(userJson);
        printWriter.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        PrintWriter printWriter = resp.getWriter();
        DeleteResponse response = userService.delete(Long.valueOf(id));
        String deleteJson = gson.toJson(response);
        printWriter.print(deleteJson);
        printWriter.flush();
    }

    private void findById(String id, PrintWriter printWriter) {
        UserResponse response = userService.findByIdResponse(Long.valueOf(id));
        String userJson = gson.toJson(response);
        printWriter.print(userJson);
        printWriter.flush();
    }

    private void findAll(PrintWriter printWriter) {
        List<UserResponse> responses = userService.findAll();
        String userJson = gson.toJson(responses);
        printWriter.print(userJson);
        printWriter.flush();
    }

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
