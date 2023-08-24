package ru.clevertec.cleverbank.exception.handler;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;

@Slf4j
@WebServlet(urlPatterns = "/exception_handler")
public class ExceptionHandlerServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(404);

        Exception exception = (Exception) req.getAttribute(ERROR_EXCEPTION);
        log.error(exception.getMessage());
        ExceptionResponse response = new ExceptionResponse(exception.getMessage());
        String json = new Gson().toJson(response);

        PrintWriter printWriter = resp.getWriter();
        printWriter.print(json);
        printWriter.flush();
    }

}
