package com.ironhack.demosecurityjwt.models.transaction;

import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.transaction.enums.TransType;
import jakarta.persistence.*;


@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    private TransType transType;

    private String authorName;
    private String description;

    public Transaction() {
    }

    public Transaction(Money amount) {
        this.amount = amount;
    }

    public Transaction(Account fromAccount, Account toAccount, Money amount, String authorName, String description) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.authorName = authorName;
        this.description = description;
    }

    public TransType getTransType() {
        return transType;
    }

    public void setTransType(TransType transType) {
        this.transType = transType;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }
}
