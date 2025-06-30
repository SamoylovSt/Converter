package ru.samoilov.convert.dto;

import java.math.BigDecimal;

public class ExchangeResultDTO {
    int id;          //exchangeRateDTo id попробовать для вывода корректного id
    CurrencyDTO baseCurrency;
    CurrencyDTO targetCurrency;
    BigDecimal rate;
    double amount;
    BigDecimal convertedAmount;

    public ExchangeResultDTO() {
    }

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

    public double getAmount() {
        return amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
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

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}
