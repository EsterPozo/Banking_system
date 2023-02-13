package com.ironhack.demosecurityjwt.services.impl;

import com.ironhack.demosecurityjwt.dtos.AccountHolderDTO;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.Address;
import com.ironhack.demosecurityjwt.models.user.Role;
import com.ironhack.demosecurityjwt.repositories.account.AccountRepository;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AccountHolderService {
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public List<AccountHolder> getOwners() {
        return accountHolderRepository.findAll();
    }

    public Optional<AccountHolder> getOwnerById(Long id) {
        if(!accountHolderRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found");
        return accountHolderRepository.findById(id);
    }

    public AccountHolder addAccountHolder(AccountHolderDTO accountHolderDTO) {
    AccountHolder accountHolder = new AccountHolder();

    accountHolder.setName(accountHolderDTO.getName());
    accountHolder.setPrimaryAddress(new Address(accountHolderDTO.getStreet(),accountHolderDTO.getCity(),accountHolderDTO.getPostalCode()));
    accountHolder.setMailingAddress(new Address(accountHolderDTO.getMailingStreet(), accountHolderDTO.getMailingCity(), accountHolderDTO.getMailingPostalCode()));

    accountHolder.setUsername(accountHolderDTO.getUsername());
    accountHolder.setPassword(accountHolderDTO.getPassword()); //need to be encrypted
    accountHolder.getRoles().add(new Role("ROLE_ACCOUNT_HOLDER"));

    return accountHolderRepository.save(accountHolder);

    }
}
