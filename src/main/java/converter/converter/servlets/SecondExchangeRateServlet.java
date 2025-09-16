package converter.converter.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import converter.converter.exceptions.ErrorResponse;
import converter.converter.service.CurrencyService;
import converter.converter.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchangeRates")
public class SecondExchangeRateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        ExchangeRateService exchangeRateService= new ExchangeRateService();


        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.getAllExchangeRates());
        } catch (SQLException e) {
            // throw new RuntimeException(e);
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("База данных недоступна"));
            // не проверено
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        ExchangeRateService exchangeRateService = new ExchangeRateService();
        CurrencyService currencyService= new CurrencyService();

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        BigDecimal rate = new BigDecimal(req.getParameter("rate"));

        if (req.getParameter("baseCurrencyCode").isEmpty()
                || req.getParameter("targetCurrencyCode").isEmpty()
                || req.getParameter("rate").isEmpty()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Отсутствует нужное поле формы"));
            return;
        }

        try {
            if(exchangeRateService.getExchangeRateForCode(baseCurrencyCode+targetCurrencyCode).getBaseCurrency()!= null
            || exchangeRateService.getExchangeRateForCode(baseCurrencyCode+targetCurrencyCode).getTargetCurrency()!= null){
                resp.setStatus(409);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Валютная пара с таким кодом уже существует"));
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            if(currencyService.getCurrencyForCode(baseCurrencyCode).getCode()==null
                    || currencyService.getCurrencyForCode(targetCurrencyCode).getCode()==null){

                resp.setStatus(404);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(" Валюта из валютной пары не существует в БД"));
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            objectMapper.writeValue(resp.getWriter(),
                    exchangeRateService.addNewExchangeRate(baseCurrencyCode, targetCurrencyCode, rate));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
