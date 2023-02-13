package com.ironhack.demosecurityjwt.models.account;


import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.enums.AccountType;
import com.ironhack.demosecurityjwt.models.account.enums.Status;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
public class CheckingAccount extends Account {
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

    public CheckingAccount() {
        setMinimumBalance(MINIMUM_BALANCE);
        setMonthlyMaintenanceFee(MONTHLY_MAINTENANCE_FEE);
        setStatus(Status.ACTIVE);
        setAccountType(AccountType.CHECKING);
        setCreationDate(LocalDateTime.now());

    }

    public CheckingAccount(AccountType accountType, Long id, Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(accountType, id, balance, primaryOwner, secondaryOwner);
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Money getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void setMonthlyMaintenanceFee(Money monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }


}
