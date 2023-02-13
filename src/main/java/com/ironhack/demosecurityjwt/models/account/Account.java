package com.ironhack.demosecurityjwt.models.account;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.enums.AccountType;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
public abstract class Account {
    /*Abstract class with (common with all accounts):
    balance
    Primary owner
    Optional Secondary owner
    penalty fee
    -add extra variable Type of account
     */

    // The penaltyFee for all accounts should be 40.
    private final static Money PENALTY_FEE = new Money(BigDecimal.valueOf(40.));
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "balance_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "balance_currency"))
    })
    private Money balance;

    @ManyToOne
    @JoinColumn(name = "primary_owner_id")
    private AccountHolder primaryOwner;

    @ManyToOne
    @JoinColumn(name = "secondary_owner_id")
    //should be option
    private AccountHolder secondaryOwner;

    public Account() {
    }

    public Account(AccountType accountType, Long id, Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        this.accountType = accountType;
        this.id = id;
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public User getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(AccountHolder secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public User getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(AccountHolder primaryOwner) {
        this.primaryOwner = primaryOwner;
    }
}
