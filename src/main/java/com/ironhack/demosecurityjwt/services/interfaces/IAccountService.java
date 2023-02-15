package com.ironhack.demosecurityjwt.services.interfaces;

import com.ironhack.demosecurityjwt.dtos.account.AccountDTO;
import com.ironhack.demosecurityjwt.dtos.account.NewBalanceDTO;
import com.ironhack.demosecurityjwt.dtos.transaction.TransactionDTO;
import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.account.Checking;
import com.ironhack.demosecurityjwt.models.account.CreditCard;
import com.ironhack.demosecurityjwt.models.account.Savings;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface IAccountService {


    List<Account> getAccounts();
    Account addAccount(Account account);

    Boolean existsAccount(Long id);
    Account getAccountById(Long id);
    Account getAccountByIdWithAuth(UserDetails userDetails, Long id);

    List<Account> getAccountsByOwner(Long idOwner);


    Account addChecking(AccountDTO accountDTO);
    Account addSavings(AccountDTO accountDTO);
    Account addCreditCard(AccountDTO accountDTO);

    Account startMoneyTransfer(UserDetails userDetails, TransactionDTO transactionDTO);
    void updateBalance(NewBalanceDTO newBalanceDTO, Long id);

}
