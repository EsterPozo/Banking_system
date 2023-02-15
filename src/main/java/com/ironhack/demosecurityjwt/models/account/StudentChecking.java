package com.ironhack.demosecurityjwt.models.account;

import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.enums.Status;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

@Entity
public class StudentChecking extends Account{
    private LocalDateTime creationDate;
    private String secretKey;
    @Enumerated(EnumType.STRING)
    private Status status;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public StudentChecking() {
        setCreationDate(LocalDateTime.now());
        setStatus(Status.ACTIVE);
    }

    public StudentChecking(Money balance, AccountHolder primaryOwner, String secretKey) {
        super(balance, primaryOwner);
        this.secretKey = secretKey;
        setCreationDate(LocalDateTime.now());
        setStatus(Status.ACTIVE);
    }

    @Override
    public Money getMinimumBalance() {
        return null;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
