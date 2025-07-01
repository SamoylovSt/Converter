package ru.samoilov.convert.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.samoilov.convert.exception.BadRequestException;
import ru.samoilov.convert.exception.ConflictException;
import ru.samoilov.convert.exception.NotFoundException;
import ru.samoilov.convert.models.Currency;
import ru.samoilov.convert.service.CurrencyService;

import java.util.List;

@RestController
@RequestMapping("/currency")

public class CurrencyController {
    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/{code}")
    public List<Currency> showCurrency(@PathVariable("code") String code) {
        // return currencyService.getCurrenciesByCode(code);
        List<Currency> currencies = currencyService.getCurrenciesByCode(code);
        if (currencies.isEmpty()) {
            throw new NotFoundException("Такая  валюта отсутствует: " + code);
        }
        return currencies;
    }

    @GetMapping("/currencies")
    public List<Currency> showAllCurrencies() {
        return currencyService.showAllCurrencies();
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Currency> save(@RequestParam("code") String code,
                                         @RequestParam("name") String name,
                                         @RequestParam("sign") String sign) {
        return currencyService.saveCurrency(code, name, sign);
    }
}
