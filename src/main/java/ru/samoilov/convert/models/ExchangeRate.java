package ru.samoilov.convert.models;

import javax.persistence.Column;
import java.math.BigDecimal;

public class ExchangeRate {


    private int id;
    @Column(name = "BaseCurrencyId")
    private int BaseCurrencyId;
    @Column(name = "TargetCurrencyId")
    private int TargetCurrencyId;
    @Column(name = "rate")
    private BigDecimal rate;

    public ExchangeRate(){
    }

    public int getId() {
        return id;
    }

    public int getBaseCurrencyId() {
        return BaseCurrencyId;
    }

    public int getTargetCurrencyId() {
        return TargetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBaseCurrencyId(int baseCurrencyId) {
        BaseCurrencyId = baseCurrencyId;
    }

    public void setTargetCurrencyId(int targetCurrencyId) {
        TargetCurrencyId = targetCurrencyId;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
