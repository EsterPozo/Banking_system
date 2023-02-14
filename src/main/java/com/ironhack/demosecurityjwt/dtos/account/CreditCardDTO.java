package com.ironhack.demosecurityjwt.dtos.account;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public class CreditCardDTO extends AccountDTO{
    @Digits(integer = 1, fraction = 4)
    @DecimalMin("0.1")
    @DecimalMax("0.2")
    private BigDecimal interestRate = new BigDecimal("0.2000");
    @Digits(integer = 6, fraction = 2)
    @DecimalMin("100")
    @DecimalMax("100000")
    private BigDecimal creditLimit = new BigDecimal("100.00");

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
}
