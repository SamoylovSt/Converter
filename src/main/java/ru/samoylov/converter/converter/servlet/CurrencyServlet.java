package ru.samoylov.converter.converter.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.samoylov.converter.converter.exception.ErrorResponse;
import ru.samoylov.converter.converter.model.Currency;
import ru.samoylov.converter.converter.dao.CurrencyDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.sql.*;
@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    ObjectMapper objectMapper = new ObjectMapper();
    CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        String code = pathInfo != null ? pathInfo.substring(1) : null;// проверить
        System.out.println(code + "  code in servlet");
        if (code.length() != 3 && !code.isEmpty()) {
            resp.setStatus(404);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Не корректный код валюты"));
        }
        if (code.isEmpty()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Код валюты отсутствует в адресе"));
        }

        try {
            if (currencyDao.getCurrencyForCode(code).getCode()==null){
                resp.setStatus(404);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Валюта не найдена"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            Currency currency = currencyDao.getCurrencyForCode(code);
            objectMapper.writeValue(resp.getWriter(), currency);
        } catch (SQLException e) {
             throw new RuntimeException(e);
        }
    }

}
