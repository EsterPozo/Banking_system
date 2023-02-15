package com.ironhack.demosecurityjwt.services.impl.account;

import com.ironhack.demosecurityjwt.dtos.transaction.TransactionDTO;
import com.ironhack.demosecurityjwt.models.account.Account;

import com.ironhack.demosecurityjwt.models.account.Checking;
import com.ironhack.demosecurityjwt.models.account.Savings;
import com.ironhack.demosecurityjwt.models.account.StudentChecking;
import com.ironhack.demosecurityjwt.models.user.ThirdParty;
import com.ironhack.demosecurityjwt.models.user.User;
import com.ironhack.demosecurityjwt.repositories.account.AccountRepository;
import com.ironhack.demosecurityjwt.repositories.user.UserRepository;
import com.ironhack.demosecurityjwt.services.impl.user.UserService;
import com.ironhack.demosecurityjwt.services.interfaces.IAccountService;
import com.ironhack.demosecurityjwt.services.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {

        @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserServiceInterface userService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private IAccountService accountService;
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

   public Boolean authMoneyTransfer(UserDetails userDetails, TransactionDTO transactionDTO) {
       //Check if account exists
        if(!accountRepository.existsById(transactionDTO.getFromAccountId()))
            throw new ResponseStatusException(BAD_REQUEST, "Origin account not found");
        //check if user has access to account
        if(!authAccountAccess(userDetails,transactionDTO.getFromAccountId()))
            throw new ResponseStatusException(UNAUTHORIZED, "Access denied");
        //access account with auth
       Account account = accountService.getAccountByIdWithAuth(userDetails, transactionDTO.getFromAccountId());

       //check type of user

       User user = userRepository.findByUsername(userDetails.getUsername());
       if (user instanceof ThirdParty) {
           ThirdParty tpu = (ThirdParty) user;
           //check if hasedkey is present
           if(transactionDTO.getHashedKey().isEmpty())
               throw new ResponseStatusException(UNAUTHORIZED, "No hashed key found");
            //check if it matches with user tpu hashedkey
           if(!transactionDTO.getHashedKey().equals(tpu.getHashedKey()))
               throw new ResponseStatusException(UNAUTHORIZED, "Failed hashedkey authorization");
           //check if account has secret key
           if(account instanceof Checking) {
               Checking checkAccount = (Checking) account;
               if(!checkAccount.getSecretKey().equals(transactionDTO.getSecretKey()))
                   throw new ResponseStatusException(UNAUTHORIZED, "Secret key not correct or not found");

           }
           if (account instanceof StudentChecking) {
               StudentChecking studAccount = (StudentChecking) account;
               if(!studAccount.getSecretKey().equals(transactionDTO.getSecretKey()))
                   throw new ResponseStatusException(UNAUTHORIZED, "Secret key not correct or not found");

           }
           if (account instanceof Savings) {
               Savings savAccount = (Savings) account;
               if(!savAccount.getSecretKey().equals(transactionDTO.getSecretKey()))
                   throw new ResponseStatusException(UNAUTHORIZED, "Secret key not correct or not found");


           }


       }
       return true;
   }
}
