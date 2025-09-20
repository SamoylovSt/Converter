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
import java.sql.SQLException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyService currencyService = new CurrencyService();

        try {
            objectMapper.writeValue(resp.getWriter(), currencyService.getAllCurrencies());
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            if (currencyService.getCurrencyForCode(req.getParameter("code")).getCode() != null) {
                resp.setStatus(409);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Валюта с таким кодом уже существует"));
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (req.getParameter("name").isEmpty()
                || req.getParameter("code").isEmpty()
                || req.getParameter("sign").isEmpty()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Отсутствует нужное поле формы"));
            return;
        }
        if (req.getParameter("name").length() > 15
                || !req.getParameter("name").matches("[a-zA-Z\\s]+")
                || !req.getParameter("code").matches("[a-zA-Z]+")
                || req.getParameter("code").length() != 3
                || req.getParameter("sign").length() > 2
        ) {
            resp.setStatus(404);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Не корректный ввод"));
            return;
        }

        if (req.getParameter("code").equals("GEY")
        ) {
            resp.setStatus(404);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Ну здравствуй пупсик"));
            return;
        }

        try {
            currencyService.add(currency);
            objectMapper.writeValue(resp.getWriter(), currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
