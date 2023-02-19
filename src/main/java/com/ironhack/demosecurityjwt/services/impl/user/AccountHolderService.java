package com.ironhack.demosecurityjwt.services.impl.user;

import com.ironhack.demosecurityjwt.dtos.user.AccountHolderDTO;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.Address;
import com.ironhack.demosecurityjwt.models.user.Role;
import com.ironhack.demosecurityjwt.models.user.User;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.repositories.user.RoleRepository;
import com.ironhack.demosecurityjwt.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AccountHolderService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private RoleRepository roleRepository;



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
    accountHolder.setPassword(passwordEncoder.encode(accountHolderDTO.getPassword()));

    return accountHolderRepository.save(accountHolder);

    }
}
