package ru.samoilov.convert.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.samoilov.convert.dao.CurrencyDAO;
import ru.samoilov.convert.exception.ConflictException;
import ru.samoilov.convert.models.Currency;

import java.util.List;

@Service
public class CurrencyService {

    private final CurrencyDAO currencyDAO;

    public CurrencyService(CurrencyDAO currencyDAO) {
        this.currencyDAO = currencyDAO;
    }

    public List<Currency> getCurrenciesByCode(String code) {
        return currencyDAO.show(code);
    }

    public List<Currency> showAllCurrencies() {
        return currencyDAO.showAllCurrencies();
    }

    public ResponseEntity<Currency> saveCurrency(String code, String name, String sign) {
        try {
//            if (currencyDAO.exestByCode(code)) {
//                return ResponseEntity.status(HttpStatus.CONFLICT).build();
//            }
            Currency currency = new Currency();
            currency.setCode(code);
            currency.setName(name);
            currency.setSign(sign);
            currencyDAO.save(currency);
            return ResponseEntity.ok(currency);
        } catch (Exception e) {
            throw  new ConflictException("Валюта с таким кодом уже существует");
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .build();
        }
    }
}
