package ru.samoilov.convert.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.samoilov.convert.dto.ExchangeRateDTO;
import ru.samoilov.convert.dto.ExchangeResultDTO;
import ru.samoilov.convert.models.ExchangeRate;
import ru.samoilov.convert.service.ExchangeRateService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/exchangeRate")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/{baseCodeTargetCode}")
    public List<ExchangeRateDTO> showDefiniteExchangeRates(@PathVariable("baseCodeTargetCode") String baseCodeTargetCode) {
        return exchangeRateService.showDefiniteExchangeRates(baseCodeTargetCode);
    }// возможно надо поменять на  @RequestParam для x-www-form-urlencoded


    @GetMapping("/exchangeRates")
    public List<ExchangeRateDTO> showAllExchangeRates() {
        return exchangeRateService.showAllexchangeRates();
    }


    @PostMapping("/exchangeRates")
    @ResponseBody
    public List<ExchangeRateDTO> saveExchangeRate(@RequestParam("baseCurrencyCode") String baseCurrencyCode,
                                                         @RequestParam("targetCurrencyCode") String targetCurrencyCode,
                                                         @RequestParam("rate") double rate) {
        return exchangeRateService.saveExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
    }

    @GetMapping("/exchange")
    public List<ExchangeResultDTO> exchange(@RequestParam("from") String from,
                                            @RequestParam("to") String to,
                                            @RequestParam("amount") int amount) {
        return exchangeRateService.exchange(from, to, amount);

    }

    @PatchMapping("/{baseCodeTargetCode}")
    public List<ExchangeRateDTO> changeRate(@PathVariable("baseCodeTargetCode") String baseCodeTargetCode,
                                            @RequestParam("rate") BigDecimal rate) {
        List<ExchangeRateDTO> result = exchangeRateService.changeRate(baseCodeTargetCode, rate);

        return result;

    }
}

//@PostMapping("/exchangeRates")
//@ResponseBody
//public ResponseEntity<ExchangeRate> saveExchangeRate(@RequestParam("baseCurrencyCode") String baseCurrencyCode,
//                                                     @RequestParam("targetCurrencyCode") String targetCurrencyCode,
//                                                     @RequestParam("rate") double rate) {
//    return exchangeRateService.saveExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
//}