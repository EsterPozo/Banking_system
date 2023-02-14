package com.ironhack.demosecurityjwt.services.impl.account;

import com.ironhack.demosecurityjwt.dtos.account.*;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.User;
import com.ironhack.demosecurityjwt.repositories.account.*;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.services.impl.user.UserService;
import com.ironhack.demosecurityjwt.services.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

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

    @Autowired
    private AuthService authService;

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        if (!accountRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        return accountRepository.findById(id).get();
    }

    //public Account getAccountByIdWithAuth()
    public Account getAccountByIdWithAuth(User user, Long id) {
        if (!accountRepository.existsById(id))
            throw new ResponseStatusException(NOT_FOUND, "Account id not found");

        //authorize access
        if (!authService.authAccountAccess(user,id))
            throw new ResponseStatusException(UNAUTHORIZED, "Access denied");

        //get the account
        Account account = accountRepository.findById(id).get();

        // apply interest
        // update last time accessed

        return accountRepository.save(account);
    }

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

    public Account addChecking(AccountDTO accountDTO) {
        if (!accountHolderRepository.existsById(accountDTO.getOwnerId()) || !accountHolderRepository.existsById(accountDTO.getOtherOwnerId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Id not valid");
        }
            AccountHolder owner = accountHolderRepository.findById(accountDTO.getOwnerId()).get();
            AccountHolder otherOwner = accountHolderRepository.findById(accountDTO.getOtherOwnerId()).get();

            //        AccountHolder owner = accountHolderRepository.findById(id).get();
//        AccountHolder otherOwner = new AccountHolder();
//        if (otherId.isPresent()) {
//             otherOwner = accountHolderRepository.findById(otherId.get()).get();
//        } else {
//            otherOwner = null;
//        }

            LocalDate today = LocalDate.now();
            int age = owner.getDateOfBirth().until(today).getYears();

            if (age < 24) {
                StudentChecking studentChecking = new StudentChecking();
                studentChecking.setPrimaryOwner(owner);
                studentChecking.setSecondaryOwner(otherOwner);
                studentChecking.setBalance(new Money(accountDTO.getBalance()));
                studentChecking.setSecretKey(accountDTO.getSecretKey());

                return studentCheckingRepository.save(studentChecking);
            }

            Checking checking = new Checking();
            checking.setPrimaryOwner(owner);
            checking.setSecondaryOwner(otherOwner);
            checking.setBalance(new Money(accountDTO.getBalance()));
            checking.setSecretKey(accountDTO.getSecretKey());

            return checkingRepository.save(checking);

        }


    /*Crear AccountDTO genérico para incluido los ids*/

    public Account addSavings(AccountDTO accountDTO) {
        if (!accountHolderRepository.existsById(accountDTO.getOwnerId()) || !accountHolderRepository.existsById(accountDTO.getOtherOwnerId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Id not valid");
        }

        AccountHolder owner = accountHolderRepository.findById(accountDTO.getOwnerId()).get();
        AccountHolder otherOwner = accountHolderRepository.findById(accountDTO.getOtherOwnerId()).get();

//        AccountHolder otherOwner = new AccountHolder();
//        if (otherId.isPresent()) {
//            otherOwner = accountHolderRepository.findById(otherId.get()).get();
//        } else {
//            otherOwner = null;
//        }

        Savings savings = new Savings();
        savings.setPrimaryOwner(owner);
        savings.setSecondaryOwner(otherOwner);
        savings.setBalance(new Money(accountDTO.getBalance()));
        savings.setSecretKey(accountDTO.getSecretKey());
        savings.setInterestRate(accountDTO.getInterestRate());
        savings.setMinimumBalance(new Money(accountDTO.getMinBalance()));

        return savingsRepository.save(savings);
    }

    public Account addCreditCard(AccountDTO accountDTO) {
        if (!accountHolderRepository.existsById(accountDTO.getOwnerId()) || !accountHolderRepository.existsById(accountDTO.getOtherOwnerId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Id not valid");
        }
        AccountHolder owner = accountHolderRepository.findById(accountDTO.getOwnerId()).get();
        AccountHolder otherOwner = accountHolderRepository.findById(accountDTO.getOtherOwnerId()).get();

//        AccountHolder owner = accountHolderRepository.findById(id).get();
//        AccountHolder otherOwner = new AccountHolder();
//        if (otherId.isPresent()) {
//            otherOwner = accountHolderRepository.findById(otherId.get()).get();
//        } else {
//            otherOwner = null;
//        }

        CreditCard creditCard = new CreditCard();
        creditCard.setPrimaryOwner(owner);
        creditCard.setSecondaryOwner(otherOwner);
        creditCard.setBalance(new Money(accountDTO.getBalance()));
        creditCard.setInterestRate(accountDTO.getInterestRate());

        return creditCardRepository.save(creditCard);

    }
    public Boolean existsAccount(Long id) {

        return accountRepository.existsById(id);
    }
    public void updateBalance(NewBalanceDTO newBalanceDTO, Long id) {
        if(!existsAccount(id))
            throw new ResponseStatusException(BAD_REQUEST, "Account id not found");

        Account account = accountRepository.findById(id).get();
        account.setBalance(new Money(newBalanceDTO.getBalance()));
        accountRepository.save(account);

    }

    //money transfer method??
}
