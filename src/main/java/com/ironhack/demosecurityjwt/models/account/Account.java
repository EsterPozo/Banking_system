package com.ironhack.demosecurityjwt.models.account;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.enums.AccountType;
import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
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

    @OneToMany(mappedBy = "toAccount", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JsonBackReference
    private List<Transaction> depositTxs;
    @OneToMany(mappedBy = "fromAccount", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JsonBackReference
    private List<Transaction> withdrawalTxs;

    private LocalDateTime lastAccessDateTime;

    public Account() {
        setBalance(new Money(BigDecimal.ZERO));


        setLastAccessDateTime(LocalDateTime.now());

        setDepositTxs(new ArrayList<>());
        setWithdrawalTxs(new ArrayList<>());
    }

//    public Account( Long id, Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
//
//        this.id = id;
//        this.balance = balance;
//        this.primaryOwner = primaryOwner;
//        this.secondaryOwner = secondaryOwner;
//    }

    public Account(Money balance, AccountHolder primaryOwner) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        setLastAccessDateTime(LocalDateTime.now());

        setDepositTxs(new ArrayList<>());
        setWithdrawalTxs(new ArrayList<>());
    }

    public Account(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        setLastAccessDateTime(LocalDateTime.now());

        setDepositTxs(new ArrayList<>());
        setWithdrawalTxs(new ArrayList<>());
    }

    public List<Transaction> getDepositTxs() {
        return depositTxs;
    }

    public void setDepositTxs(List<Transaction> depositTxs) {
        this.depositTxs = depositTxs;
    }

    public List<Transaction> getWithdrawalTxs() {
        return withdrawalTxs;
    }

    public void setWithdrawalTxs(List<Transaction> withdrawalTxs) {
        this.withdrawalTxs = withdrawalTxs;
    }

    public LocalDateTime getLastAccessDateTime() {
        return lastAccessDateTime;
    }

    public void setLastAccessDateTime(LocalDateTime lastAccessDateTime) {
        this.lastAccessDateTime = lastAccessDateTime;
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

    public abstract Money getMinimumBalance();

    public Money getPenaltyFee() {
        return PENALTY_FEE;
    }

    public void addWithdrawalTransaction(Transaction transaction) {
        if (withdrawalTxs.contains(transaction))
            return;
        withdrawalTxs.add(transaction);
        transaction.setFromAccount(this);

        // decrease money
        getBalance().decreaseAmount(transaction.getAmount());
    }

    public void addDepositTransaction(Transaction transaction) {
        System.out.println("entro den addDeposit");
        if (depositTxs.contains(transaction))
            return;
        depositTxs.add(transaction);
        transaction.setToAccount(this);

        // increase money
        getBalance().increaseAmount(transaction.getAmount());
    }
}
