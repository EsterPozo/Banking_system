package com.ironhack.demosecurityjwt.dtos.account;

public class CheckingDTO extends AccountDTO  {

    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
