package com.ironhack.demosecurityjwt.dtos.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public class NewBalanceDTO {

    @Digits(integer = 6, fraction = 2)
    @DecimalMin(value = "0")
    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
