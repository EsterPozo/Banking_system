package com.ironhack.demosecurityjwt.dtos.account;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class AccountDTO {
    @DecimalMin(value = "0")
    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
