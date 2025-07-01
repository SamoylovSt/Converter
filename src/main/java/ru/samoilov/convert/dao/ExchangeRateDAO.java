package ru.samoilov.convert.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.samoilov.convert.dto.CurrencyDTO;
import ru.samoilov.convert.dto.ExchangeRateDTO;
import ru.samoilov.convert.models.Currency;
import ru.samoilov.convert.models.ExchangeRate;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExchangeRateDAO {
    private final JdbcTemplate jdbcTemplate;

    public ExchangeRateDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public CurrencyDTO findCurrencyForId(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM currencies WHERE id=?",//перечислить поля
                new Object[]{id}, new BeanPropertyRowMapper<>(CurrencyDTO.class));
    }

    public boolean exestByCodeBaseCurrency(String BaseCurrency) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM currencies WHERE code=?", Integer.class, BaseCurrency);
        return count == null && count == 0;
    }

    public int getCurrencyIdForCode(String code) {
        return jdbcTemplate.queryForObject("SELECT id FROM currencies WHERE code=?",
                Integer.class, code);
    }

    public List<ExchangeRate> showDefiniteExchangeRatesDAO(String baseCode, String targetCode) {
        return jdbcTemplate.query("select er.id, er.base_currency_id, er.target_currency_id, er.rate\n" +
                        "from exchange_rates er\n" +
                        "         JOIN currencies cr\n" +
                        "              on er.base_currency_id = cr.id\n" +
                        "         Join currencies targetcr on er.target_currency_id = targetcr.id\n" +
                        "where cr.code = ? AND targetcr.code =?",
                new BeanPropertyRowMapper<>(ExchangeRate.class), baseCode, targetCode
        );
    }

    public List<ExchangeRate> showAllexchangeRates() {
        return jdbcTemplate.query("SELECT id,base_currency_id,target_currency_id, rate FROM exchange_rates",//перечислить поля
                new BeanPropertyRowMapper<>(ExchangeRate.class));
    }

    public void exchangeRateSave(ExchangeRate exchangeRate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, exchangeRate.getBaseCurrencyId());
            ps.setInt(2, exchangeRate.getTargetCurrencyId());
            ps.setBigDecimal(3, exchangeRate.getRate());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            exchangeRate.setId((int) key.longValue());
        }
    }

    public void changeRate(BigDecimal rate, int id) {
        jdbcTemplate.update("UPDATE exchange_rates set rate=? where id=?", rate, id);

    }
}
