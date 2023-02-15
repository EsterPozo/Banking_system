package com.ironhack.demosecurityjwt.services.impl.account;

import com.ironhack.demosecurityjwt.models.account.Account;

import com.ironhack.demosecurityjwt.models.user.User;
import com.ironhack.demosecurityjwt.repositories.account.AccountRepository;
import com.ironhack.demosecurityjwt.repositories.user.UserRepository;
import com.ironhack.demosecurityjwt.services.impl.user.UserService;
import com.ironhack.demosecurityjwt.services.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

        @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserServiceInterface userService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public Boolean authAccountAccess( UserDetails userDetails, Long idAccount) {

       Account account = accountRepository.findById(idAccount).get();
       User user = userRepository.findByUsername(userDetails.getUsername());
        //findbyusername en repository
       if(user.getId().equals(account.getPrimaryOwner().getId())) {
           return true;
       }

       return account.getSecondaryOwner() != null && user.getId().equals(account.getSecondaryOwner().getId());
   }
}
