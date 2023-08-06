package org.example;


public class Currency {
    private Integer ID;
    private String Code;
    private String Name;
    private String Sign;

    public Currency(String code, String name, String sign) {
        Code = code;
        Name = name;
        Sign = sign;
    }

    public Integer getID() {
        return ID;
    }

    public String getCode() {
        return Code;
    }

    public String getName() {
        return Name;
    }

    public String getSign() {
        return Sign;
    }

    public void setCode(String code) {
        Code = code;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setSign(String sign) {
        Sign = sign;
    }
}
