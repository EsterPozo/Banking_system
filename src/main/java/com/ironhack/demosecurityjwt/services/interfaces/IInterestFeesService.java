package com.ironhack.demosecurityjwt.services.interfaces;

import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.account.Checking;

public interface IInterestFeesService {

    void applyMonthlyInterest(Account account);
    void applyMonthlyFee(Checking account);
    Account applyInterestsFeesService(Account account);
}
