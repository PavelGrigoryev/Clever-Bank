package ru.clevertec.cleverbank.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@WebServlet(urlPatterns = "/download")
public class DownloadServlet extends HttpServlet {


    /**
     * Переопределяет метод doGet, который обрабатывает GET-запросы к ресурсу /download. Через него можно скачать чеки
     * и выписки по транзакциям.
     *
     * @param req  объект HttpServletRequest, представляющий запрос
     * @param resp объект HttpServletResponse, представляющий ответ
     * @throws IOException если возникает ошибка ввода-вывода при работе с потоком печати
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String file = req.getParameter("file");
        if (Objects.nonNull(file)) {
            resp.setHeader("Content-Disposition", "attachment; filename=\"%s\"".formatted(file));
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF-8");
            try (ServletOutputStream outputStream = resp.getOutputStream();
                 InputStream inputStream = DownloadServlet.class.getClassLoader()
                         .getResourceAsStream("check/%s".formatted(file))) {
                if (Objects.nonNull(inputStream)) {
                    outputStream.write(inputStream.readAllBytes());
                }
            }
        }
    }

}
