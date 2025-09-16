package converter.converter.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import converter.converter.exceptions.ErrorResponse;
import converter.converter.models.Currency;
import converter.converter.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.sql.*;


@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyService currencyService = new CurrencyService();


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
            if (currencyService.getCurrencyForCode(code).getCode()==null){
                resp.setStatus(404);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Валюта не найдена"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            Currency currency = currencyService.getCurrencyForCode(code);
            objectMapper.writeValue(resp.getWriter(), currency);
        } catch (SQLException e) {
             throw new RuntimeException(e);
             //не сделал код ответа с 500
        }

    }


}
