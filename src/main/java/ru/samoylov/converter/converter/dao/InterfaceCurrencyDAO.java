package ru.samoylov.converter.converter.dao;

import ru.samoylov.converter.converter.model.Currency;

import java.sql.SQLException;
import java.util.List;

public interface InterfaceCurrencyDAO {

    void add(Currency currency) throws SQLException;

    List<Currency> getAllCurrencies() throws SQLException, InterruptedException;

    Currency getCurrencyForCode(String code) throws SQLException;

    void update(Currency currency) throws SQLException;

}
