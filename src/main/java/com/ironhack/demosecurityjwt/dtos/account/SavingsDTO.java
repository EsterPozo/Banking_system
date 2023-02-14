package com.ironhack.demosecurityjwt.dtos.account;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public class SavingsDTO extends CheckingDTO{
    @Digits(integer = 1, fraction = 4)
    @DecimalMin("0")
    @DecimalMax("0.5")
    private BigDecimal interestRate = BigDecimal.valueOf(0.0025);

    @Digits(integer = 4, fraction = 2)
    @DecimalMin("100")
    @DecimalMax(value = "1000")
    private BigDecimal minBalance = BigDecimal.valueOf(1000.0);

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(BigDecimal minBalance) {
        this.minBalance = minBalance;
    }
}
