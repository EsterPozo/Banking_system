package com.ironhack.demosecurityjwt.models.transaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.transaction.enums.TransType;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    @JsonBackReference
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    @JsonBackReference
    private Account toAccount;

    @Embedded
    private Money amount;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private TransType transType;

    private String authorName;
    private String description;

    public Transaction() {

        setTimestamp(LocalDateTime.now());
    }

    public Transaction(Money amount) {
        this();
        this.amount = amount;
        setTimestamp(LocalDateTime.now());
    }

    public Transaction(Account fromAccount, Account toAccount, Money amount, String authorName, String description) {
        this.amount = amount;
        setFromAccount(fromAccount);
        setToAccount(toAccount);
        setAuthorName(authorName);
        setDescription(description);
        setTimestamp(LocalDateTime.now());
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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
        if (toAccount == null || toAccount == this.toAccount)
            return;
        this.toAccount = toAccount;
        System.out.println("entro en setTo account");
        toAccount.addDepositTransaction(this);
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
        if (fromAccount == null || fromAccount == this.fromAccount)
            return;
        this.fromAccount = fromAccount;
        fromAccount.addWithdrawalTransaction(this);
    }
}
