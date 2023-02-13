package com.ironhack.demosecurityjwt.services.impl;

import com.ironhack.demosecurityjwt.repositories.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountHolderService {
    @Autowired
    private AccountRepository accountRepository;


}
