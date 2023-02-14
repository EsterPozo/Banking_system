package com.ironhack.demosecurityjwt.services.impl.account;

import com.ironhack.demosecurityjwt.dtos.account.CheckingDTO;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.repositories.account.*;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;


import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private SavingsRepository savingsRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        if (!accountRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        return accountRepository.findById(id).get();
    }

    //public Account getAccountByIdWithAuth()

    public List<Account> getAccountsByOwner(Long idOwner) {
        if (!accountHolderRepository.existsById(idOwner))
            throw new ResponseStatusException(BAD_REQUEST, "Id not valid");

        AccountHolder owner = accountHolderRepository.findById(idOwner).get();
        return accountRepository.findByPrimaryOwnerOrSecondaryOwner(owner, owner);
    }

    public Account addAccount(Account account) {
        if (account instanceof Checking) {
            return checkingRepository.save((Checking) account);
        } else if (account instanceof StudentChecking) {
            return studentCheckingRepository.save((StudentChecking) account);
        } else if( account instanceof Savings) {
            return savingsRepository.save((Savings) account);
        } else if (account instanceof CreditCard) {
            return creditCardRepository.save((CreditCard) account);
        } else {
            throw new ResponseStatusException(BAD_REQUEST, "The account type does not exist");
        }
    }

    public Account addChecking(CheckingDTO checkingDTO, Long id, Optional<Long> otherId) {
        if (!accountHolderRepository.existsById(id) || (otherId.isPresent()) && !accountHolderRepository.existsById(otherId.get()))
            throw new ResponseStatusException(BAD_REQUEST, "Id not valid");

        AccountHolder owner = accountHolderRepository.findById(id).get();
        AccountHolder otherOwner = new AccountHolder();
        if (otherId.isPresent()) {
             otherOwner = accountHolderRepository.findById(otherId.get()).get();
        } else {
            otherOwner = null;
        }

        LocalDate today = LocalDate.now();
        int age = owner.getDateOfBirth().until(today).getYears();

        if (age < 24) {
            StudentChecking studentChecking = new StudentChecking();
            studentChecking.setPrimaryOwner(owner);
            studentChecking.setSecondaryOwner(otherOwner);
            studentChecking.setBalance(new Money(checkingDTO.getBalance()));
            studentChecking.setSecretKey(checkingDTO.getSecretKey());

            return studentCheckingRepository.save(studentChecking);
        }

        Checking checking = new Checking();
        checking.setPrimaryOwner(owner);
        checking.setSecondaryOwner(otherOwner);
        checking.setBalance(new Money(checkingDTO.getBalance()));
        checking.setSecretKey(checkingDTO.getSecretKey());

        return checkingRepository.save(checking);

    }

}
