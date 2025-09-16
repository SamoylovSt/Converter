package converter.converter.service;

import converter.converter.dao.ConnectionPool;
import converter.converter.dao.InterfaceExchangeRateDAO;
import converter.converter.dto.ExchangeDTO;
import converter.converter.dto.ExchangeRateDTO;
import converter.converter.models.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService extends ConnectionPool implements InterfaceExchangeRateDAO {

    Connection connection = getConnection();


    @Override
    public ExchangeRateDTO addNewExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        ExchangeRateDTO exchangeRateDTOresult = new ExchangeRateDTO();
        CurrencyService currencyService = new CurrencyService();
        String SQL = "insert into exchangerates (base_currency_id, target_currency_id, rate)\n" +
                "VALUES (\n" +
                "        ?,\n" +
                "        ?,\n" +
                "        ?\n" +
                "       )";

        PreparedStatement preparedStatement = null;
        exchangeRateDTOresult.setBaseCurrency(currencyService.getCurrencyForCode(baseCurrencyCode));
        exchangeRateDTOresult.setTargetCurrency(currencyService.getCurrencyForCode(targetCurrencyCode));
        exchangeRateDTOresult.setRate(rate);
        try {
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1,
                    currencyService.getCurrencyForCode(baseCurrencyCode).getId());
            preparedStatement.setInt(2,
                    currencyService.getCurrencyForCode(targetCurrencyCode).getId());
            preparedStatement.setBigDecimal(3, rate);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRateDTOresult;

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
        }
//        finally {
//            if (statement != null) {
//                statement.close();
//            }
//            if (connection != null) {
//                connection.close();
//            }
//
//
//        }
        return exchangeRates;

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
            System.out.println(fromCurrency + " " + toCurrency + " " + amount);
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

    public BigDecimal getCrossExchangeRate(String fromCurrency,
                                           String toCurrency,
                                           BigDecimal amount) throws SQLException {

        ExchangeDTO USDfrom = new ExchangeDTO();
        ExchangeDTO USDto = new ExchangeDTO();

//        USDfrom = exchange("USD", toCurrency, amount);
//        USDto = exchange("USD", fromCurrency, amount);
        USDfrom = getExchangeDTOAndCheckUSDForCrossСourse("USD", toCurrency, amount);
        USDto = getExchangeDTOAndCheckUSDForCrossСourse("USD", fromCurrency, amount);

        BigDecimal rateFromCurrency = USDfrom.getRate();
        System.out.println(rateFromCurrency + " rateFromCurrency");

        BigDecimal rateToCurrency = USDto.getRate();
        System.out.println(rateToCurrency + " rateToCurrency");
        BigDecimal crossExchange = rateFromCurrency.divide(rateToCurrency, 2, RoundingMode.HALF_UP);
        return crossExchange;
    }

    public ExchangeDTO getExchangeDTOAndCheckUSDForCrossСourse(String fromCurrency,
                                                               String toCurrency, BigDecimal amount) throws SQLException {
        ExchangeRateDTO exchangeRateDTOForExchange = getExchangeRateForCode(fromCurrency + toCurrency);
        BigDecimal rate = null;
        BigDecimal convertedAmount = null;

        if (hasExchangeRate(exchangeRateDTOForExchange)) {
            rate = exchangeRateDTOForExchange.getRate();
            convertedAmount = rate.multiply(amount);
        } else if (!hasExchangeRate(exchangeRateDTOForExchange)) {
            String from = toCurrency;
            String to = fromCurrency;
            exchangeRateDTOForExchange = getExchangeRateForCode(from + to);
            if (hasExchangeRate(exchangeRateDTOForExchange)) {
                rate = exchangeRateDTOForExchange.getRate();
                BigDecimal reverse = BigDecimal.ONE.divide(rate, 3, RoundingMode.HALF_UP);
                System.out.println(reverse);
                rate = reverse;
                convertedAmount = amount.multiply(reverse);
            }
        }
        CurrencyService currencyService = new CurrencyService();
        ExchangeDTO exchangeDTOResult = new ExchangeDTO();
        exchangeDTOResult.setBaseCurrency(currencyService.getCurrencyForCode(fromCurrency));
        exchangeDTOResult.setTargetCurrency(currencyService.getCurrencyForCode(toCurrency));
        exchangeDTOResult.setRate(rate);
        exchangeDTOResult.setAmount(amount);
        exchangeDTOResult.setConvertedAmount(convertedAmount);

        return exchangeDTOResult;
    }
}



