package com.ironhack.demosecurityjwt.repositories.account;

import com.ironhack.demosecurityjwt.models.account.Savings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, Long> {
}
