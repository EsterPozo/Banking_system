package com.ironhack.demosecurityjwt.services.impl.transaction;

import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import com.ironhack.demosecurityjwt.repositories.transaction.TransactionRepository;
import com.ironhack.demosecurityjwt.services.impl.account.AccountService;
import com.ironhack.demosecurityjwt.services.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserServiceInterface userService;

    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionByAccount(UserDetails userDetails, Long idAccount) {
        if(!accountService.existsAccount(idAccount))
            throw new ResponseStatusException(NOT_FOUND, "Account not found");

       // Account account = accountService.getA

        return null;
    }
}
