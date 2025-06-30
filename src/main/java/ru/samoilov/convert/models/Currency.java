package ru.samoilov.convert.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


public class Currency {

    private int id;
    @Column(name = "code")
    private String code;
    @Column(name = "name")// name
   //@JsonProperty("name")
    private String name;
    @Column(name = "sign")
    private String sign;

    public Currency() {
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
