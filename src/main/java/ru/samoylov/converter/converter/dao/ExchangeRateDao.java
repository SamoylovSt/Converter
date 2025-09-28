package ru.samoylov.converter.converter.dao;

import ru.samoylov.converter.converter.util.ConnectionPool;
import ru.samoylov.converter.converter.dto.ExchangeDTO;
import ru.samoylov.converter.converter.dto.ExchangeRateDTO;
import ru.samoylov.converter.converter.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao implements InterfaceExchangeRateDAO {

    private final static String ADD_SQL = "insert into exchangerates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
    private final static String GET_ALL_SQL = "SELECT * FROM exchangerates";
    private final static String GET_FOR_CODE_SQL = "SELECT id, base_currency_id, target_currency_id, rate FROM exchangerates WHERE base_currency_id=? and target_currency_id=?";
    private final static String UPDATE_SQL = "UPDATE exchangerates SET rate=? WHERE id=?";

    ConnectionPool connectionPool = new ConnectionPool();

    @Override
    public ExchangeRateDTO addNewExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        ExchangeRateDTO exchangeRateDTOresult = new ExchangeRateDTO();
        CurrencyDao currencyDao = new CurrencyDao();

        exchangeRateDTOresult.setBaseCurrency(currencyDao.getCurrencyForCode(baseCurrencyCode));
        exchangeRateDTOresult.setTargetCurrency(currencyDao.getCurrencyForCode(targetCurrencyCode));
        exchangeRateDTOresult.setRate(rate);
        var connection = connectionPool.get();
        try {
            var preparedStatement = connection.prepareStatement(ADD_SQL);
            preparedStatement.setInt(1,
                    currencyDao.getCurrencyForCode(baseCurrencyCode).getId());
            preparedStatement.setInt(2,
                    currencyDao.getCurrencyForCode(targetCurrencyCode).getId());
            preparedStatement.setBigDecimal(3, rate);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
        return exchangeRateDTOresult;

    }

    public List<ExchangeRateDTO> getAllExchangeRates() throws SQLException {
        List<ExchangeRateDTO> exchangeRates = new ArrayList<>();
        CurrencyDao currencyDao = new CurrencyDao();
        var connection = connectionPool.get();
        try {
            var statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_SQL);
            while (resultSet.next()) {
                ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
                var baseCurrency = currencyDao.getCurrencyForId(resultSet.getInt("base_currency_id"));
                var targetCurrency = currencyDao.getCurrencyForId(resultSet.getInt("target_currency_id"));

                exchangeRateDTO.setId(resultSet.getInt("id"));
                exchangeRateDTO.setBaseCurrency(baseCurrency);
                exchangeRateDTO.setTargetCurrency(targetCurrency);
                exchangeRateDTO.setRate(resultSet.getBigDecimal("rate"));

                exchangeRates.add(exchangeRateDTO);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
        return exchangeRates;
    }

    @Override
    public ExchangeRateDTO getExchangeRateForCode(String code) throws SQLException {
        CurrencyDao currencyDao = new CurrencyDao();
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
        String firstCurrencyCode = code.substring(0, 3);
        String secondCorrencyCode = code.substring(3);
        Currency baseCurrency = currencyDao.getCurrencyForCode(firstCurrencyCode);
        Currency targetCurrency = currencyDao.getCurrencyForCode(secondCorrencyCode);
        var connection = connectionPool.get();
        try {
            var preparedStatement = connection.prepareStatement(GET_FOR_CODE_SQL);
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
        } finally {
            connection.close();
        }
        return exchangeRateDTO;
    }

    @Override
    public ExchangeRateDTO update(String code, BigDecimal newRate) throws SQLException {
        ExchangeRateDTO exchangeRateDTO = getExchangeRateForCode(code);
        int exchangeRateId = exchangeRateDTO.getId();

        try {
            var preparedStatement = connectionPool.get().prepareStatement(UPDATE_SQL);
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

        CurrencyDao currencyDao = new CurrencyDao();
        ExchangeDTO exchangeDTOResult = new ExchangeDTO();
        exchangeDTOResult.setBaseCurrency(currencyDao.getCurrencyForCode(fromCurrency));//exchangeRateDTOForExchange.getBaseCurrency()
        exchangeDTOResult.setTargetCurrency(currencyDao.getCurrencyForCode(toCurrency));//exchangeRateDTOForExchange.getTargetCurrency()
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

        var USDfrom = getExchangeDTOAndCheckUSDForCrossСourse("USD", toCurrency, amount);
        var USDto = getExchangeDTOAndCheckUSDForCrossСourse("USD", fromCurrency, amount);

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
        CurrencyDao currencyDao = new CurrencyDao();
        ExchangeDTO exchangeDTOResult = new ExchangeDTO();
        exchangeDTOResult.setBaseCurrency(currencyDao.getCurrencyForCode(fromCurrency));
        exchangeDTOResult.setTargetCurrency(currencyDao.getCurrencyForCode(toCurrency));
        exchangeDTOResult.setRate(rate);
        exchangeDTOResult.setAmount(amount);
        exchangeDTOResult.setConvertedAmount(convertedAmount);

        return exchangeDTOResult;
    }
}



