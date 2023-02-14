package com.ironhack.demosecurityjwt.dtos.account;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public class AccountDTO {

    private Long ownerId;
    private Long otherOwnerId;
    @DecimalMin(value = "0")
    private BigDecimal balance;

    private String secretKey;

    @Digits(integer = 1, fraction = 4)
    @DecimalMin("0.1")
    @DecimalMax("0.2")
    private BigDecimal interestRate = new BigDecimal("0.2000");
    @Digits(integer = 6, fraction = 2)
    @DecimalMin("100")
    @DecimalMax("100000")
    private BigDecimal creditLimit = new BigDecimal("100.00");

    @Digits(integer = 4, fraction = 2)
    @DecimalMin("100")
    @DecimalMax(value = "1000")
    private BigDecimal minBalance = BigDecimal.valueOf(1000.0);

    public String getSecretKey() {
        return secretKey;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getOtherOwnerId() {
        return otherOwnerId;
    }

    public void setOtherOwnerId(Long otherOwnerId) {
        this.otherOwnerId = otherOwnerId;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

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

    public BigDecimal getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(BigDecimal minBalance) {
        this.minBalance = minBalance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
