package converter.converter.service;

import converter.converter.dao.ConnectionPool;
import converter.converter.dao.InterfaceCurrencyDAO;
import converter.converter.exceptions.ErrorResponse;
import converter.converter.models.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyService extends ConnectionPool implements InterfaceCurrencyDAO {

    Connection connection = getConnection();

    @Override
    public void add(Currency currency) throws SQLException {
        PreparedStatement preparedStatement = null;
        String sql = "INSERT INTO currencies (code,full_name,sign) VALUES(?,?,?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }


    @Override
    public List<Currency> getAllCurrencies() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        String SQL = "SELECT * FROM currencies";

        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
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
        }
        return currencies;

    }

    @Override
    public Currency getCurrencyForCode(String code) throws SQLException {
        String SQL = "SELECT id, code, full_name, sign FROM currencies WHERE code=?";
        Currency currency = new Currency();
      if(code.length()!=3){
          return currency;
      }
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                currency.setId(resultSet.getInt("id"));
                currency.setCode(resultSet.getString("code"));
                currency.setName(resultSet.getString("full_name"));
                currency.setSign(resultSet.getString("sign"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        if (currency.getCode()==null){
//
//             throw new RuntimeException();
//        }
        return currency;
    }

    public Currency getCurrencyForId(int id) throws SQLException {
        String SQL = "SELECT id, code, full_name, sign FROM currencies WHERE id=?";

        Currency currency = new Currency();

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                currency.setId(resultSet.getInt("id"));
                currency.setCode(resultSet.getString("code"));
                currency.setName(resultSet.getString("full_name"));
                currency.setSign(resultSet.getString("sign"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currency;
    }

    @Override
    public void update(Currency currency) throws SQLException {
        PreparedStatement preparedStatement = null;
        String SQL = "UPDATE currencies\n" +
                "set code='?',\n" +
                "    full_name='?',\n" +
                "    sign='?'\n" +
                "where code=?";
        try {
            preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(1, currency.getSign());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}