package com.ironhack.demosecurityjwt.repositories.transaction;

import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
}
