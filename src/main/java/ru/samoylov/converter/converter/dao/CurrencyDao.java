package ru.samoylov.converter.converter.dao;

import ru.samoylov.converter.converter.model.Currency;
import ru.samoylov.converter.converter.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CurrencyDao implements InterfaceCurrencyDAO {

    private final static String SAVE_SQL = "INSERT INTO currencies (code,full_name,sign) VALUES(?,?,?)";
    private final static String GET_ALL_CURRENCIES_SQL = "SELECT * FROM currencies";
    private final static String GET_CURRENCY_FOR_CODE_SQL="SELECT id, code, full_name, sign FROM currencies WHERE code=?";
    private final static String GET_CURRENCY_FOR_ID_SQL ="SELECT id, code, full_name, sign FROM currencies WHERE id=?";
    private final static String UPDATE_SQL="UPDATE currencies SET code=?, full_name=?, sign=? WHERE code=?";

    ConnectionPool connectionPool = new ConnectionPool();
    @Override
    public void add(Currency currency) throws SQLException {
        var connection = connectionPool.get();
        try {
            var preparedStatement = connection.prepareStatement(SAVE_SQL);
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
    }

    @Override
    public List<Currency> getAllCurrencies() throws SQLException, InterruptedException {
        List<Currency> currencies = new ArrayList<>();
        var connection = connectionPool.get();
        try {
            var statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_CURRENCIES_SQL);
            while (resultSet.next()) {
                Currency currency = new Currency();

                currency.setId(resultSet.getInt("id"));
                currency.setCode(resultSet.getString("code"));
                currency.setName(resultSet.getString("full_name"));
                currency.setSign(resultSet.getString("sign"));
                currencies.add(currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
        return currencies;
    }

    @Override
    public Currency getCurrencyForCode(String code) throws SQLException {
        Currency currency = new Currency();
        if (code.length() != 3) {
            return currency;
        }
        var connection = connectionPool.get();
        try {
            var preparedStatement = connection.prepareStatement(GET_CURRENCY_FOR_CODE_SQL);
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                currency.setId(resultSet.getInt("id"));
                currency.setCode(resultSet.getString("code"));
                currency.setName(resultSet.getString("full_name"));
                currency.setSign(resultSet.getString("sign"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
        return currency;
    }

    public Currency getCurrencyForId(int id) throws SQLException {
        Currency currency = new Currency();
        var connection = connectionPool.get();
        try {
            var preparedStatement = connection.prepareStatement(GET_CURRENCY_FOR_ID_SQL);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                currency.setId(resultSet.getInt("id"));
                currency.setCode(resultSet.getString("code"));
                currency.setName(resultSet.getString("full_name"));
                currency.setSign(resultSet.getString("sign"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
        return currency;
    }

    @Override
    public void update(Currency currency) throws SQLException {

        var connection = connectionPool.get();
        try {
            var preparedStatement = connection.prepareStatement(UPDATE_SQL);

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(1, currency.getSign());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}