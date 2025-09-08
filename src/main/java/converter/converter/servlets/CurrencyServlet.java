package converter.converter.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import converter.converter.models.Currency;
import converter.converter.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.sql.*;


@WebServlet(urlPatterns = {"/currencies", "/currency/*"})
public class CurrencyServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyService currencyService = new CurrencyService();

        String pathInfo = req.getPathInfo();
        String code = pathInfo != null ? pathInfo.substring(1) : null;// проверить
        System.out.println(code);
        if (code == null) {
            try {
                objectMapper.writeValue(resp.getWriter(), currencyService.getAllCurrencies());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Currency currency = currencyService.getCurrencyForCode(code);
                objectMapper.writeValue(resp.getWriter(), currency);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyService currencyService = new CurrencyService();
        Currency currency = new Currency();

        currency.setName(req.getParameter("name"));
        currency.setCode(req.getParameter("code"));
        currency.setSign(req.getParameter("sign"));
        try {
            currencyService.add(currency);
            objectMapper.writeValue(resp.getWriter(),currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    //коды ответа

}
