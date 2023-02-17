package com.ironhack.demosecurityjwt.models.account;

import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.enums.Status;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Entity
public class Savings extends Account {
    /*Savings are identical to Checking accounts except that they

    Do NOT have a monthlyMaintenanceFee
    Do have an interestRate*/
    private static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("0.0025", new MathContext(4,RoundingMode.HALF_EVEN));
    private final static Money MINIMUM_BALANCE = new Money(BigDecimal.valueOf(1000L));


    @Column(precision = 19, scale = 4,columnDefinition = "decimal(19,4)")
    private BigDecimal interestRate;
    @CreationTimestamp
    private LocalDateTime creationDate;
    private String secretKey;
    private LocalDateTime interestAddedDateTime;

//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "currency", column = @Column(name = "interestRate_currency")),
//            @AttributeOverride(name = "amount", column = @Column(name = "interesRate_amount"))
//    })
//    private Money interestRate;

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
        setInterestAddedDateTime(LocalDateTime.now());
    }

    public Savings(Money balance, AccountHolder primaryOwner, String secretKey) {
        super(balance, primaryOwner);
        this.secretKey = secretKey;
        setInterestRate(DEFAULT_INTEREST_RATE);
        setMinimumBalance(MINIMUM_BALANCE);
        setStatus(Status.ACTIVE);
        setCreationDate(LocalDateTime.now());
        setInterestAddedDateTime(LocalDateTime.now());
    }

    //    public Savings(BigDecimal interestRate) {
//        setInterestRate(interestRate);
//        setMinimumBalance(MINIMUM_BALANCE);
//        setStatus(Status.ACTIVE);
//        setCreationDate(LocalDateTime.now());
//    }
//
//    public Savings(Money minimumBalance) {
//        setInterestRate(interestRate);
//        setMinimumBalance(MINIMUM_BALANCE);
//        setStatus(Status.ACTIVE);
//        setCreationDate(LocalDateTime.now());
//    }

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

        if (interestRate.compareTo(BigDecimal.valueOf(0.5)) > 0) {
            throw new ResponseStatusException(BAD_REQUEST,"Interest rate can't be bigger than 0.5" );
                    }
      //  this.interestRate = new BigDecimal(interestRate.doubleValue(),new MathContext(4,RoundingMode.HALF_EVEN));
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
        if(minimumBalance.getAmount().compareTo(BigDecimal.valueOf(100)) < 0 ) {
            throw new ResponseStatusException(BAD_REQUEST,"Minimum balance can't be lower than 100");

        }
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

    public Integer getYearsSinceLastInterestAdded() {
        long years = ChronoUnit.YEARS.between(getInterestAddedDateTime(), LocalDateTime.now());
        return (int) years;
    }
    public BigDecimal getAnnualInterestRate() {
        return getInterestRate();
    }


    public Money getLastInterestGenerated() {
        BigDecimal earnedInterests = BigDecimal.ZERO;
       // BigDecimal interest = getAnnualInterestRate();
        BigDecimal interest = getInterestRate().setScale(6);
        System.out.println("interest: " + interest);
        for(int i=0; i<getYearsSinceLastInterestAdded(); i++) {
            earnedInterests = earnedInterests.add((getBalance().getAmount()).multiply(interest));
        }
        System.out.println("earned Interest " + earnedInterests);
        return new Money(earnedInterests);
    }

    public void updateInterestAddedDateTime() {
        setInterestAddedDateTime(LocalDateTime.now());
    }



}
