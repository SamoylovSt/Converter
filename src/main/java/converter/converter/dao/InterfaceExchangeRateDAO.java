package converter.converter.dao;

import converter.converter.dto.ExchangeRateDTO;
import converter.converter.models.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceExchangeRateDAO {

    void add (ExchangeRate exchangeRate);

    List<ExchangeRateDTO> getAllExchangeRates() throws SQLException;

    ExchangeRateDTO getExchangeRateForCode(String code) throws SQLException;

    ExchangeRateDTO update(String code, BigDecimal newRate) throws SQLException;


}
