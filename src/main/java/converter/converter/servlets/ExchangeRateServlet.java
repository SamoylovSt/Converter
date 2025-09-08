package converter.converter.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import converter.converter.dto.ExchangeDTO;
import converter.converter.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/exchangeRates","/exchangeRate/*","/exchange"})
public class ExchangeRateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        ExchangeRateService exchangeRateService = new ExchangeRateService();
        ExchangeDTO fromResponse= new ExchangeDTO();
        String uri = req.getRequestURI();
        String contextPath =req.getContextPath();
        String pathWithoutContext =uri.substring(contextPath.length());
        String [] pathParts= pathWithoutContext.split("/");
        String firstSegment= pathParts[1];
        System.out.println(firstSegment);


        String pathInfo = req.getPathInfo();
        String code = pathInfo != null ? pathInfo.substring(1) : null;// проверить


        if( firstSegment.equals("exchangeRates")){
            try {
                objectMapper.writeValue(resp.getWriter(),exchangeRateService.getAllExchangeRates());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else if(firstSegment.equals("exchangeRate")) {
            try {
                objectMapper.writeValue(resp.getWriter(),exchangeRateService.getExchangeRateForCode(code));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else  {
           String from= req.getParameter("from");
           String to= req.getParameter("to");
           String amount=req.getParameter("amount");

            try {
                fromResponse=exchangeRateService.exchange(from,to,new BigDecimal(amount));
                objectMapper.writeValue(resp.getWriter(),fromResponse);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper= new ObjectMapper();
        ExchangeRateService exchangeRateService = new ExchangeRateService();
        String rate=req.getParameter("rate");

        String pathInfo = req.getPathInfo();
        String code = pathInfo != null ? pathInfo.substring(1) : null;// проверить
        System.out.println(code);

        try {
            objectMapper.writeValue(resp.getWriter(),exchangeRateService.update(code,new BigDecimal(rate)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
