package com.ironhack.demosecurityjwt.models.account;

import com.ironhack.demosecurityjwt.models.Money;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class CreditCard extends Account {
    private static final Double VALID_MIN_CREDIT_LIMIT = 100.0;
    private static final Double VALID_MAX_CREDIT_LIMIT = 100000.0;
    private static final Double VALID_MIN_INTEREST_RATE = 0.1;
    private static final Double VALID_MAX_INTEREST_RATE = 0.2;

    private static final Money DEFAULT_CREDIT_LIMIT = new Money(BigDecimal.valueOf(VALID_MIN_CREDIT_LIMIT));
    private static final BigDecimal DEFAULT_INTEREST_RATE = BigDecimal.valueOf(VALID_MAX_INTEREST_RATE);

    private Money creditLimit;
    private BigDecimal interestRate;

    public CreditCard() {
        setCreditLimit(DEFAULT_CREDIT_LIMIT);
        setInterestRate(DEFAULT_INTEREST_RATE);
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
