package com.ironhack.demosecurityjwt.services.impl.transaction;

import com.ironhack.demosecurityjwt.dtos.transaction.TransactionDTO;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import com.ironhack.demosecurityjwt.models.transaction.enums.TransType;
import com.ironhack.demosecurityjwt.repositories.account.AccountRepository;

import com.ironhack.demosecurityjwt.services.interfaces.IAccountService;
import com.ironhack.demosecurityjwt.services.interfaces.IMoneyTransferService;
import com.ironhack.demosecurityjwt.services.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class MoneyTransferService implements IMoneyTransferService {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ITransactionService transactionService;

    public Account doMoneyTransfer(TransactionDTO transactionDTO) {

        //get accounts involved
        Account origin = accountService.getAccountById(transactionDTO.getFromAccountId());
        Account destination = accountService.getAccountById(transactionDTO.getToAccountId());

        //check fraud detection

        //check enough funds in origin
        BigDecimal currentBalance = origin.getBalance().getAmount();
        BigDecimal transferAmount = transactionDTO.getAmount();
        if(transferAmount.compareTo(currentBalance) > 0)
            throw new ResponseStatusException(BAD_REQUEST, "Amount exceeds balance of account");

        //check if penalty fee has to be deduced later
        BigDecimal result = currentBalance.subtract(transferAmount);
        boolean applyPenaltyFee = currentBalance.compareTo(origin.getMinimumBalance().getAmount()) >0 && result.compareTo(origin.getMinimumBalance().getAmount()) < 0;

        // make transaction
        Transaction transaction = new Transaction(new Money(transferAmount));
        transaction.setTransType(TransType.MONEY_TRANSFER);
        transaction.setFromAccount(origin);
        transaction.setToAccount(destination);
        transaction.setAuthorName(transactionDTO.getName());
        transaction.setDescription(transactionDTO.getDescription());
        transactionService.addTransaction(transaction);

        //make penalty fee transaction
        if(applyPenaltyFee) {
            Transaction deductionTransaction = new Transaction(origin.getPenaltyFee());
            transaction.setTransType(TransType.PENALTY_FEE);
            deductionTransaction.setFromAccount(origin);
            deductionTransaction.setAuthorName(transactionDTO.getName());
            deductionTransaction.setDescription("Penalty fee deduction");
            transactionService.addTransaction(deductionTransaction);
        }

        return accountRepository.save(origin);

    }
}
