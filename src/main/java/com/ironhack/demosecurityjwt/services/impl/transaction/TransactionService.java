package com.ironhack.demosecurityjwt.services.impl.transaction;

import com.ironhack.demosecurityjwt.dtos.transaction.TransactionDTO;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import com.ironhack.demosecurityjwt.models.transaction.enums.TransType;
import com.ironhack.demosecurityjwt.repositories.account.AccountRepository;
import com.ironhack.demosecurityjwt.repositories.transaction.TransactionRepository;

import com.ironhack.demosecurityjwt.services.interfaces.IAccountService;
import com.ironhack.demosecurityjwt.services.interfaces.ITransactionService;
import com.ironhack.demosecurityjwt.services.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserServiceInterface userService;
    @Autowired
    private AccountRepository accountRepository;

    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionByAccount(UserDetails userDetails, Long idAccount) {
        if(!accountService.existsAccount(idAccount))
            throw new ResponseStatusException(NOT_FOUND, "Account not found");

       Account account = accountService.getAccountByIdWithAuth(userDetails, idAccount);
       return transactionRepository.findByFromAccountOrToAccount(account, account);

    }

    public Transaction addTransaction(Transaction transaction) {

        Transaction newTransaction = transactionRepository.saveAndFlush(transaction);

        if (transaction.getFromAccount() != null)
            accountRepository.save(transaction.getFromAccount());
        if(transaction.getToAccount() != null)
            accountRepository.save(transaction.getToAccount());
        return newTransaction;
    }


}
