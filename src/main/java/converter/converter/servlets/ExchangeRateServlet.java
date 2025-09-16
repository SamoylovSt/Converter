package converter.converter.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import converter.converter.dto.ExchangeDTO;
import converter.converter.exceptions.ErrorResponse;
import converter.converter.service.CurrencyService;
import converter.converter.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

@WebServlet(urlPatterns = {"/exchangeRate/*", "/exchange"})
public class ExchangeRateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        ExchangeRateService exchangeRateService = new ExchangeRateService();
        CurrencyService currencyService = new CurrencyService();
        ExchangeDTO fromResponse = new ExchangeDTO();
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String pathWithoutContext = uri.substring(contextPath.length());
        String[] pathParts = pathWithoutContext.split("/");
        String firstSegment = pathParts[1];
        System.out.println(firstSegment);


        String pathInfo = req.getPathInfo();
        String code = pathInfo != null ? pathInfo.substring(1) : null;// проверить

        if (firstSegment.equals("exchangeRate")) {
            if (code.length() != 6 && !code.isEmpty()) {
                resp.setStatus(400);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Коды валют пары отсутствуют в адресе"));
            }

            try {
                if (exchangeRateService.getExchangeRateForCode(code).getBaseCurrency()==null){
                    resp.setStatus(404);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Обменный курс для пары не найден"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try {
                objectMapper.writeValue(resp.getWriter(), exchangeRateService.getExchangeRateForCode(code));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {

            String from = req.getParameter("from");
            String to = req.getParameter("to");
            String amount = req.getParameter("amount");

            try {
                fromResponse = exchangeRateService.exchange(from, to, new BigDecimal(amount));
                objectMapper.writeValue(resp.getWriter(), fromResponse);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        ExchangeRateService exchangeRateService = new ExchangeRateService();


        BufferedReader reader = req.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        reader.close();
        String requestBodyString = requestBody.toString();
        String rate = requestBodyString.substring(5);



        String pathInfo = req.getPathInfo();
        String code = pathInfo != null ? pathInfo.substring(1) : null;// проверить
        System.out.println(code);

        if (rate.length()<1){
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Отсутствует нужное поле формы"));
            return;
        }
        try {
            if (exchangeRateService.getExchangeRateForCode(code).getBaseCurrency()==null
            || exchangeRateService.getExchangeRateForCode(code).getTargetCurrency()==null ){
                resp.setStatus(404);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Валютная пара отсутствует в базе данных"));
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.update(code, new BigDecimal(rate)));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
