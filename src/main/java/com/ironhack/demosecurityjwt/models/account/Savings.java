package com.ironhack.demosecurityjwt.models.account;

import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.enums.Status;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Savings extends Account{
    /*Savings are identical to Checking accounts except that they

    Do NOT have a monthlyMaintenanceFee
    Do have an interestRate*/
    private static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("0.0025");
    private final static Money MINIMUM_BALANCE = new Money(BigDecimal.valueOf(250L));

    private BigDecimal interestRate;
    private LocalDateTime creationDate;
    private String secretKey;



    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "min_balance_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "min_balance_amount"))
    })
    private Money minimumBalance;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Savings() {
        setInterestRate(DEFAULT_INTEREST_RATE);
        setMinimumBalance(MINIMUM_BALANCE);
        setStatus(Status.ACTIVE);
        setCreationDate(LocalDateTime.now());
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
