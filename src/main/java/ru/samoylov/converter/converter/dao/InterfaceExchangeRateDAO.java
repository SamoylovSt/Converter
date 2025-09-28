package ru.samoylov.converter.converter.dao;

import ru.samoylov.converter.converter.dto.ExchangeRateDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceExchangeRateDAO {

    ExchangeRateDTO addNewExchangeRate (String baseCurrencyCode,String targetCurrencyCode, BigDecimal rate) throws SQLException;

    List<ExchangeRateDTO> getAllExchangeRates() throws SQLException;

    ExchangeRateDTO getExchangeRateForCode(String code) throws SQLException;

    ExchangeRateDTO update(String code, BigDecimal newRate) throws SQLException;


}
