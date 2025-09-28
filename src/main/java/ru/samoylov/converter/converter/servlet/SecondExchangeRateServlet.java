package ru.samoylov.converter.converter.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.samoylov.converter.converter.exception.ErrorResponse;
import ru.samoylov.converter.converter.dao.CurrencyDao;
import ru.samoylov.converter.converter.dao.ExchangeRateDao;
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
    ObjectMapper objectMapper = new ObjectMapper();
    ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateDao.getAllExchangeRates());
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("База данных недоступна"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            if (exchangeRateDao.getExchangeRateForCode(baseCurrencyCode + targetCurrencyCode).getBaseCurrency() != null
                    || exchangeRateDao.getExchangeRateForCode(baseCurrencyCode + targetCurrencyCode).getTargetCurrency() != null) {
                resp.setStatus(409);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Валютная пара с таким кодом уже существует"));
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            if (currencyDao.getCurrencyForCode(baseCurrencyCode).getCode() == null
                    || currencyDao.getCurrencyForCode(targetCurrencyCode).getCode() == null) {

                resp.setStatus(404);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(" Валюта из валютной пары не существует в БД"));
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            objectMapper.writeValue(resp.getWriter(),
                    exchangeRateDao.addNewExchangeRate(baseCurrencyCode, targetCurrencyCode, rate));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
