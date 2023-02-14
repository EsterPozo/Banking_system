package com.ironhack.demosecurityjwt.models.account;


import com.ironhack.demosecurityjwt.models.Money;

import com.ironhack.demosecurityjwt.models.account.enums.Status;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import jakarta.persistence.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Entity
public class Checking extends Account {
    /*Checking Accounts should have:

    A balance (account)
    A secretKey +
    A PrimaryOwner (account)
    An optional SecondaryOwner (account)
    A minimumBalance +
    A penaltyFee (account)
    A monthlyMaintenanceFee +
    A creationDate +
    A status (FROZEN, ACTIVE) +*/

    // Checking accounts should have a minimumBalance of 250 and a monthlyMaintenanceFee of 12
    private final static Money MINIMUM_BALANCE = new Money(BigDecimal.valueOf(250L));
    private final static Money MONTHLY_MAINTENANCE_FEE = new Money(BigDecimal.valueOf(12L));

    private String secretKey;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "min_balance_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "min_balance_amount"))
    })
    private Money minimumBalance;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "monthly_fee_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "monthly_fee_amount"))
    })
    private Money monthlyMaintenanceFee;



    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime creationDate;

    public Checking() {
        setMinimumBalance(MINIMUM_BALANCE);
        setMonthlyMaintenanceFee(MONTHLY_MAINTENANCE_FEE);
        setStatus(Status.ACTIVE);

        setCreationDate(LocalDateTime.now());

    }

    public Checking(Long id, Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super( id, balance, primaryOwner, secondaryOwner);
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Money getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void setMonthlyMaintenanceFee(Money monthlyMaintenanceFee) {
//        if (monthlyMaintenanceFee.getAmount().compareTo(BigDecimal.valueOf(12)) <0)
//            throw new ResponseStatusException(BAD_REQUEST, "Monthly maintenance fee can't be less than 12");
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        if (minimumBalance.getAmount().compareTo(BigDecimal.valueOf(250)) < 0)
            throw new ResponseStatusException(BAD_REQUEST, "Min. balance can't be less than 250");

        this.minimumBalance = minimumBalance;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }


}
