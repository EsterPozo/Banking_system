package com.ironhack.demosecurityjwt.repositories.transaction;

import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findByFromAccountOrToAccount(Account fromAccount, Account toAccount);

}
