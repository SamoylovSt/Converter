package ru.samoylov.converter.converter.model;

public class Currency {

    private int id;
    private String code;
    private String name;
    private String sign;

    public Currency(String code, String fullName, String sign) {
        this.code = code;
        this.name = fullName;
        this.sign = sign;
    }

    public Currency() {
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", fullName='" + name + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
