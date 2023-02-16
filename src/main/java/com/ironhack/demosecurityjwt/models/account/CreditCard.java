package com.ironhack.demosecurityjwt.models.account;

import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import jakarta.persistence.Entity;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Entity
public class CreditCard extends Account {

    private static final Money DEFAULT_CREDIT_LIMIT = new Money(BigDecimal.valueOf(100.0));
    private static final BigDecimal DEFAULT_INTEREST_RATE = BigDecimal.valueOf(0.2);

    private Money creditLimit;
    private BigDecimal interestRate;

    private LocalDateTime interestAddedDateTime;

    public CreditCard() {
        setCreditLimit(DEFAULT_CREDIT_LIMIT);
        setInterestRate(DEFAULT_INTEREST_RATE);
    }

    @Override
    public Money getMinimumBalance() {
        return null;
    }

    //both constructors should be together??
    public CreditCard(Money creditLimit) {
        setCreditLimit(creditLimit);
        setInterestRate(DEFAULT_INTEREST_RATE);
    }

    public CreditCard(BigDecimal interestRate) {
        setCreditLimit(DEFAULT_CREDIT_LIMIT);
        setInterestRate(interestRate);
    }

    public CreditCard(Money balance, AccountHolder primaryOwner) {
        super(balance, primaryOwner);
        setCreditLimit(DEFAULT_CREDIT_LIMIT);
        setInterestRate(DEFAULT_INTEREST_RATE);
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        if(creditLimit.getAmount().compareTo(BigDecimal.valueOf(100)) < 0 || creditLimit.getAmount().compareTo(BigDecimal.valueOf(100000)) > 0 ) {
            throw new ResponseStatusException(BAD_REQUEST,"credit limit can't be higher than 100.000");

        }
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public LocalDateTime getInterestAddedDateTime() {
        return interestAddedDateTime;
    }

    public void setInterestAddedDateTime(LocalDateTime interestAddedDateTime) {
        this.interestAddedDateTime = interestAddedDateTime;
    }

    public void setInterestRate(BigDecimal interestRate) {
        if (interestRate.compareTo(BigDecimal.valueOf(0.2)) > 0 || interestRate.compareTo(BigDecimal.valueOf(0.1)) < 0  )
            throw new ResponseStatusException(BAD_REQUEST,"interest rate can't be lower than 0.1");

        this.interestRate = interestRate;
    }

    public Integer getYearsSinceLastInterestAdded() {
        long years = ChronoUnit.YEARS.between(getInterestAddedDateTime(), LocalDateTime.now());
        return (int) years;
    }
    public BigDecimal getAnnualInterestRate() {
        return getInterestRate();
    }


    public Money getLastInterestGenerated() {
        BigDecimal earnedInterests = BigDecimal.ZERO;
        BigDecimal interest = getAnnualInterestRate();
        for(int i=0; i<getYearsSinceLastInterestAdded(); i++) {
            earnedInterests = earnedInterests.add((getBalance().getAmount()).multiply(interest));
        }
        return new Money(earnedInterests);
    }

    public void updateInterestAddedDateTime() {
        setInterestAddedDateTime(LocalDateTime.now());
    }

}
