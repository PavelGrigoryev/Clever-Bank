package ru.clevertec.cleverbank.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = "/*", asyncSupported = true)
public class EncodingFilter implements Filter {

    /**
     * Переопределяет метод doFilter, чтобы установить кодировку и тип содержимого ответа в формате JSON и UTF-8.
     *
     * @param request  объект ServletRequest, содержащий данные запроса
     * @param response объект ServletResponse, содержащий данные ответа
     * @param chain    объект FilterChain, который позволяет передать запрос и ответ дальше по цепочке фильтров
     * @throws IOException      если произошла ошибка ввода-вывода
     * @throws ServletException если произошла ошибка сервлета
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

}
