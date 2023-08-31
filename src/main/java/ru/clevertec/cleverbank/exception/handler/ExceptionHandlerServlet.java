package ru.clevertec.cleverbank.exception.handler;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.cleverbank.exception.badrequest.BadRequestException;
import ru.clevertec.cleverbank.exception.conflict.LocalDateParseException;
import ru.clevertec.cleverbank.exception.conflict.ValidationException;
import ru.clevertec.cleverbank.exception.notfound.NotFoundException;

import java.io.IOException;
import java.io.PrintWriter;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;

@WebServlet(urlPatterns = "/exception_handler")
public class ExceptionHandlerServlet extends HttpServlet {

    private final transient Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Exception exception = (Exception) req.getAttribute(ERROR_EXCEPTION);
        PrintWriter printWriter = resp.getWriter();

        if (exception instanceof NotFoundException) {
            resp.setStatus(404);
            printExceptionResponse(exception.getMessage(), printWriter);
        } else if (exception instanceof BadRequestException) {
            resp.setStatus(400);
            printExceptionResponse(exception.getMessage(), printWriter);
        } else if (exception instanceof ValidationException) {
            resp.setStatus(409);
            if (exception instanceof LocalDateParseException) {
                printExceptionResponse(exception.getMessage(), printWriter);
            } else {
                printWriter.print(exception.getMessage());
                printWriter.flush();
            }
        } else {
            resp.setStatus(500);
            printExceptionResponse(exception.getMessage(), printWriter);
        }
    }

    private void printExceptionResponse(String message, PrintWriter printWriter) {
        ExceptionResponse response = new ExceptionResponse(message);
        String json = gson.toJson(response);
        printWriter.print(json);
        printWriter.flush();
    }

}
