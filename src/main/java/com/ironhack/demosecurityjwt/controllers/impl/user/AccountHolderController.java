package com.ironhack.demosecurityjwt.controllers.impl.user;

import com.ironhack.demosecurityjwt.dtos.user.AccountHolderDTO;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.services.impl.user.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountHolderController {

    @Autowired
    private AccountHolderService accountHolderService;

    @GetMapping("/bank/users/owners")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountHolder> getOwners() {

        return accountHolderService.getOwners();
    }

    @GetMapping("/bank/users/owners/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountHolder getOwnerById(@PathVariable Long id) {
        return accountHolderService.getOwnerById(id).get();
    }

    @PostMapping("/bank/users/owners/ah")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolder addAccountHolder(@RequestBody AccountHolderDTO accountHolderDTO) {
        return accountHolderService.addAccountHolder(accountHolderDTO);
    }

}
