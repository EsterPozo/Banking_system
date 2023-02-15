package com.ironhack.demosecurityjwt.controllers.impl.transaction;

import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import com.ironhack.demosecurityjwt.services.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {
    @Autowired
    private ITransactionService transactionService;

    @GetMapping("/bank/transactions")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getTransactions() {
        return transactionService.getTransactions();
    }

    @GetMapping("/accounts/{id}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getTransactionByAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long idAccount) {
        return transactionService.getTransactionByAccount(userDetails,idAccount);
    }
}
