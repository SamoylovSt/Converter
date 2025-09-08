package converter.converter.service;

import converter.converter.dao.ConnectionPool;
import converter.converter.dao.InterfaceExchangeRateDAO;
import converter.converter.dto.ExchangeDTO;
import converter.converter.dto.ExchangeRateDTO;
import converter.converter.models.Currency;
import converter.converter.models.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class ExchangeRateService extends ConnectionPool implements InterfaceExchangeRateDAO {

    Connection connection = getConnection();


    @Override
    public void add(ExchangeRate exchangeRate) {

    }

    public List<ExchangeRateDTO> getAllExchangeRates() throws SQLException {
        List<ExchangeRateDTO> exchangeRates = new ArrayList<>();
        String SQL = "SELECT * FROM exchangerates";
        CurrencyService currencyService = new CurrencyService();
        Currency baseCurrency = new Currency();
        Currency targetCurrency = new Currency();

        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
                baseCurrency = currencyService.getCurrencyForId(resultSet.getInt("base_currency_id"));
                targetCurrency = currencyService.getCurrencyForId(resultSet.getInt("target_currency_id"));

                exchangeRateDTO.setId(resultSet.getInt("id"));
                exchangeRateDTO.setBaseCurrency(baseCurrency);
                exchangeRateDTO.setTargetCurrency(targetCurrency);
                exchangeRateDTO.setRate(resultSet.getBigDecimal("rate"));

                exchangeRates.add(exchangeRateDTO);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }

            return exchangeRates;
        }


    }

    @Override
    public ExchangeRateDTO getExchangeRateForCode(String code) throws SQLException {
        CurrencyService currencyService = new CurrencyService();
        String SQL = "SELECT id, base_currency_id, target_currency_id, rate FROM exchangerates WHERE base_currency_id=? and target_currency_id=?";
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
        PreparedStatement preparedStatement = null;
        String firstCurrencyCode = code.substring(0, 3);
        String secondCorrencyCode = code.substring(3);
        Currency baseCurrency = currencyService.getCurrencyForCode(firstCurrencyCode);
        Currency targetCurrency = currencyService.getCurrencyForCode(secondCorrencyCode);


        try {
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, baseCurrency.getId());
            preparedStatement.setInt(2, targetCurrency.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                exchangeRateDTO.setId(resultSet.getInt("id"));
                exchangeRateDTO.setBaseCurrency(baseCurrency);
                exchangeRateDTO.setTargetCurrency(targetCurrency);
                exchangeRateDTO.setRate(resultSet.getBigDecimal("rate"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRateDTO;
    }

    @Override
    public ExchangeRateDTO update(String code, BigDecimal newRate) throws SQLException {
        ExchangeRateDTO exchangeRateDTO = getExchangeRateForCode(code);
        int exchangeRateId = exchangeRateDTO.getId();
        String SQL = "UPDATE exchangerates\n" +
                "set rate=?\n" +
                "where id=?";

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setBigDecimal(1, newRate);
            preparedStatement.setInt(2, exchangeRateId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getExchangeRateForCode(code);
    }


    public ExchangeDTO exchange(String fromCurrency, String toCurrency, BigDecimal amount) throws SQLException {
        ExchangeRateDTO exchangeRateDTOForExchange = getExchangeRateForCode(fromCurrency + toCurrency);
        BigDecimal rate = null;
        BigDecimal convertedAmount = null;

        if (hasExchangeRate(exchangeRateDTOForExchange)) {
            System.out.println("estb");
            // crossExchange(fromCurrency,toCurrency,amount);
            System.out.println(fromCurrency + " " + toCurrency + "" + amount);
            rate = exchangeRateDTOForExchange.getRate();
            convertedAmount = rate.multiply(amount);
        } else if (!hasExchangeRate(exchangeRateDTOForExchange)) {
            System.out.println("netu");
            String from = toCurrency;
            String to = fromCurrency;
            exchangeRateDTOForExchange = getExchangeRateForCode(from + to);
            if (hasExchangeRate(exchangeRateDTOForExchange)) {
                System.out.println("estb");
                rate = exchangeRateDTOForExchange.getRate();
                BigDecimal reverse = BigDecimal.ONE.divide(rate, 3, RoundingMode.HALF_UP);
                System.out.println(reverse);
                rate = reverse;
                convertedAmount = amount.multiply(reverse);
            } else {
                System.out.println("cross course");
                convertedAmount = amount.multiply(getCrossExchangeRate(fromCurrency, toCurrency, amount));
                rate = getCrossExchangeRate(fromCurrency, toCurrency, amount);
            }

        }


//        if (hasExchangeRate(exchangeRateDTOForExchange)){
//            System.out.println("cross course");
//            convertedAmount = amount.multiply(getCrossExchangeRate(fromCurrency,toCurrency,amount));
//            rate=getCrossExchangeRate(fromCurrency,toCurrency,amount);
//        }
//        convertedAmount = amount.multiply(getCrossExchangeRate(fromCurrency, toCurrency, amount));
//
//         rate=getCrossExchangeRate(fromCurrency,toCurrency,amount);
        CurrencyService currencyService = new CurrencyService();
        ExchangeDTO exchangeDTOResult = new ExchangeDTO();
        exchangeDTOResult.setBaseCurrency(currencyService.getCurrencyForCode(fromCurrency));//exchangeRateDTOForExchange.getBaseCurrency()
        exchangeDTOResult.setTargetCurrency(currencyService.getCurrencyForCode(toCurrency));//exchangeRateDTOForExchange.getTargetCurrency()
        exchangeDTOResult.setRate(rate);
        exchangeDTOResult.setAmount(amount);
        exchangeDTOResult.setConvertedAmount(convertedAmount);

        return exchangeDTOResult;
    }

    public boolean hasExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        if (exchangeRateDTO.getTargetCurrency() == null || exchangeRateDTO.getBaseCurrency() == null) {
            return false;
        } else {
            return true;
        }
    }

    public BigDecimal getCrossExchangeRate(String fromCurrency, String toCurrency, BigDecimal amount) throws SQLException {

        //usd+from=rate1 вызовом echange-> .getRate
        //usd+to=rate2 вызовом echange-> .getRate
        //result=rate1/rate2
        ExchangeDTO USDfrom = exchange("USD", toCurrency, amount);
        ExchangeDTO USDto = exchange("USD", fromCurrency, amount);
   //     ExchangeRateDTO USDfrom = getExchangeRateForCode("USD" + fromCurrency);
//        if (USDfrom.getRate() == null) {
//            USDfrom =   getExchangeRateForCode(fromCurrency + "USD");
//        }
        //      ExchangeRateDTO USDto = getExchangeRateForCode("USD"+toCurrency ) ;
//        if (USDto.getRate() == null) {
//            USDto = getExchangeRateForCode( toCurrency+"USD" );
//        }
        BigDecimal rateFromCurrency = USDfrom.getRate();
        System.out.println(rateFromCurrency + " rateFromCurrency");

        BigDecimal rateToCurrency = USDto.getRate();
        System.out.println(rateToCurrency + " rateToCurrency");
        BigDecimal crossExchange = rateFromCurrency.divide(rateToCurrency, 3, RoundingMode.HALF_UP);
        return crossExchange;

    }

}



