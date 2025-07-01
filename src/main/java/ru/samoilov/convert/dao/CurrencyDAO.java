package ru.samoilov.convert.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.samoilov.convert.models.Currency;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
public class CurrencyDAO {


    private final JdbcTemplate jdbcTemplate;

    public CurrencyDAO(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Currency> show(String code) {

        return jdbcTemplate.query("SELECT * FROM currencies WHERE code=?",
                new BeanPropertyRowMapper<>(Currency.class), code
        );

    }

    public List<Currency> showAllCurrencies() {
        return jdbcTemplate.query("SELECT * FROM currencies",
                new BeanPropertyRowMapper<>(Currency.class));
    }

    public boolean exestByCode(String code) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM currencies WHERE code=?", Integer.class, code);
        return count != null && count > 0;
    }

    public void save(Currency currency) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO currencies (code, name, sign) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, currency.getCode());
            ps.setString(2, currency.getName());
            ps.setString(3, currency.getSign());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            currency.setId((int) key.longValue());
        }
    }

}
