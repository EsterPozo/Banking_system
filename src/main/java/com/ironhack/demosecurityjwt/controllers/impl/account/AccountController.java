package com.ironhack.demosecurityjwt.controllers.impl.account;

import com.ironhack.demosecurityjwt.dtos.account.CheckingDTO;
import com.ironhack.demosecurityjwt.dtos.account.CreditCardDTO;
import com.ironhack.demosecurityjwt.dtos.account.NewBalanceDTO;
import com.ironhack.demosecurityjwt.dtos.account.SavingsDTO;
import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.account.Savings;
import com.ironhack.demosecurityjwt.models.user.User;
import com.ironhack.demosecurityjwt.services.impl.account.AccountService;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/bank/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/bank/accounts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Account getAccount(Long id) {
        return accountService.getAccountById(id);
    }

    //get account with customerdetails (auth) - something missing
    @GetMapping("/accounts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Account getAccount(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return accountService.getAccountByIdWithAuth(user, id);
    }

    @GetMapping("/bank/users/owners/{id}/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAccountByOwner(@PathVariable Long id) {
        return accountService.getAccountsByOwner(id);
    }

    @PostMapping("/bank/accounts/checking/{id1}/{id2}")
    @ResponseStatus(HttpStatus.CREATED)
    public Account addChecking(@RequestBody CheckingDTO checkingDTO,@PathVariable Long id1, @PathVariable Optional<Long> id2) {
        return accountService.addChecking(checkingDTO,id1, id2);
    }

    @PostMapping("/bank/accounts/savings/{id1}/{id2}")
    @ResponseStatus(HttpStatus.CREATED)
    public Account addSavings(@RequestBody SavingsDTO savingsDTO, @PathVariable Long id1, @PathVariable Optional<Long> id2) {
        return accountService.addSavings(savingsDTO, id1, id2);
    }

    @PostMapping("/bank/accounts/creditcard/{id1}/{id2}")
    @ResponseStatus(HttpStatus.CREATED)
    public Account addCreditCard(@RequestBody CreditCardDTO creditCardDTO, @PathVariable Long id1, @PathVariable Optional<Long> id2) {
        return accountService.addCreditCard(creditCardDTO, id1, id2);
    }

    @PatchMapping("/bank/accounts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateBalance(NewBalanceDTO newBalanceDTO, Long id) {
        accountService.updateBalance(newBalanceDTO,id);
    }

    //transferMoney pending
}
