package com.ironhack.demosecurityjwt.controllers.impl.account;

import com.ironhack.demosecurityjwt.dtos.account.*;
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

    @PostMapping("/bank/accounts/checking/")
    @ResponseStatus(HttpStatus.CREATED)
    public Account addChecking(@RequestBody AccountDTO accountDTO) {
        return accountService.addChecking(accountDTO);
    }

    @PostMapping("/bank/accounts/savings/")
    @ResponseStatus(HttpStatus.CREATED)
    public Account addSavings(@RequestBody AccountDTO accountDTO) {
        return accountService.addSavings(accountDTO);
    }

    @PostMapping("/bank/accounts/creditcard/")
    @ResponseStatus(HttpStatus.CREATED)
    public Account addCreditCard(@RequestBody AccountDTO accountDTO) {
        return accountService.addCreditCard(accountDTO);
    }

    @PatchMapping("/bank/accounts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateBalance(NewBalanceDTO newBalanceDTO, Long id) {
        accountService.updateBalance(newBalanceDTO,id);
    }

    //transferMoney pending
}
