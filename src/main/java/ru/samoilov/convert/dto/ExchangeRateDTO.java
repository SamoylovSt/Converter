package ru.samoilov.convert.dto;

import java.math.BigDecimal;

public class ExchangeRateDTO {
    int id;
    CurrencyDTO baseCurrency;
    CurrencyDTO targetCurrency;
    BigDecimal rate;

    public  ExchangeRateDTO(){  }

    public int getId() {
        return id;
    }

    public CurrencyDTO getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyDTO getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBaseCurrency(CurrencyDTO baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setTargetCurrency(CurrencyDTO targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
