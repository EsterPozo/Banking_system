package com.ironhack.demosecurityjwt.services.interfaces;

import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface ITransactionService {

    List<Transaction> getTransactions();
    List<Transaction> getTransactionByAccount(UserDetails userDetails, Long idAccount);
    Transaction addTransaction(Transaction transaction);
}
