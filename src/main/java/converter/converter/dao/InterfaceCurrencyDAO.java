package converter.converter.dao;

import converter.converter.models.Currency;

import java.sql.SQLException;
import java.util.List;

public interface InterfaceCurrencyDAO {
    //create
    void add(Currency currency) throws SQLException;

    //read
    List<Currency> getAllCurrencies() throws SQLException;

    Currency getCurrencyForCode(String code) throws SQLException;

    //update
    void update(Currency currency) throws SQLException;

    //delete


    //такие интерфейсы для каждой модели

}
