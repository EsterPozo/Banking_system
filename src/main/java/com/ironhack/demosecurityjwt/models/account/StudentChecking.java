package com.ironhack.demosecurityjwt.models.account;

import com.ironhack.demosecurityjwt.models.account.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

@Entity
public class StudentChecking extends Account{
    private LocalDateTime creationDate;
    @Enumerated(EnumType.STRING)
    private Status status;

    public StudentChecking() {
        setCreationDate(LocalDateTime.now());
        setStatus(Status.ACTIVE);
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
