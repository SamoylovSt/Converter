package ru.samoylov.converter.converter.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import ru.samoylov.converter.converter.exception.ErrorResponse;


import java.io.IOException;
import java.io.PrintWriter;

@WebFilter("/*")
public class huiFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ObjectMapper om = new ObjectMapper();

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            if (servletResponse instanceof HttpServletResponse) {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                if (!httpResponse.isCommitted()) {
                    // Устанавливаем Content-Type для JSON
                    httpResponse.setContentType("application/json");
                    // Устанавливаем статус ошибки 500
                    httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                    // Формируем JSON-ответ
                    String jsonResponse = "{\"message\": \"Ошибка сервера\"}";
                    ErrorResponse errorResponse= new ErrorResponse("ошибка сервера1");
                    om.writeValue(httpResponse.getWriter(),errorResponse);
//                    PrintWriter out = httpResponse.getWriter();
//                    out.write(jsonResponse);
//                    out.flush();

                    e.printStackTrace();
                } else {
                    // Если ответ уже отправлен, просто пробрасываем исключение дальше
                    throw e;
                }
            } else {
                // Если это не HTTP-ответ, просто пробрасываем исключение дальше
                throw e;
            }
        }
    }
}