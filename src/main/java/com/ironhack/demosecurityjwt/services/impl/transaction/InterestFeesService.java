package com.ironhack.demosecurityjwt.services.impl.transaction;

import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import com.ironhack.demosecurityjwt.models.transaction.enums.TransType;

import com.ironhack.demosecurityjwt.repositories.account.CreditCardRepository;
import com.ironhack.demosecurityjwt.services.interfaces.IAccountService;
import com.ironhack.demosecurityjwt.services.interfaces.IInterestFeesService;
import com.ironhack.demosecurityjwt.services.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class InterestFeesService implements IInterestFeesService {

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IAccountService accountService;
    @Autowired
    private CreditCardRepository creditCardRepository;

    public Account applyInterestsFeesService(Account account) {
        if (account instanceof Checking) {
            Checking checkAccount = (Checking) account;
            applyMonthlyFee(checkAccount);
            return accountService.addAccount(checkAccount);
        } else if (account instanceof Savings) {
            Savings savAccount = (Savings) account;
            System.out.println("interest latest added time" + ((Savings) account).getInterestAddedDateTime());
            applyAnnualInterest(savAccount);
            return accountService.addAccount(savAccount);
        } else if(account instanceof CreditCard) {
            CreditCard credAccount = (CreditCard) account;
            applyMonthlyInterest(credAccount);
            return accountService.addAccount(credAccount);
        }else if(account instanceof StudentChecking) {
            StudentChecking studAccount = (StudentChecking) account;
            return accountService.addAccount(studAccount);
        } else {
            throw new ResponseStatusException(BAD_REQUEST, "The account type does not exist");
        }


    }

    public void applyMonthlyFee(Checking account) {
        LocalDateTime feeAppliedDateTime = account.getMonthlyFeeAppliedDateTime();
        LocalDateTime now = LocalDateTime.now();
      //  if (account.getMonthsSinceLastMonthlyFeeDeduction() > 0) {
            if (ChronoUnit.MONTHS.between(feeAppliedDateTime, now) > 0) {
            //long months = ChronoUnit.MONTHS.between(account.getMonthlyFeeAppliedDateTime(), LocalDateTime.now());
            //new transaction to reflect the deduction of fees

            Transaction transaction = new Transaction(account.getMonthlyMaintenanceFee());
            transaction.setTransType(TransType.MAINTENANCE_FEE);
            transaction.setFromAccount(account);
            transaction.setAuthorName("Ironbank");
            String dateString = feeAppliedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            transaction.setDescription("Maintenance fee deductions since " + dateString);
            transactionService.addTransaction(transaction);

            //update account info
            account.updateMonthlyFeeAppliedDateTime();
        }
    }

    public void applyAnnualInterest(Account account) {
        if (account instanceof Savings) {
            Savings savAccount = (Savings) account;
            LocalDateTime interestAddedDateTime = savAccount.getInterestAddedDateTime();
            System.out.println("a??os :" + savAccount.getYearsSinceLastInterestAdded());
            if (savAccount.getYearsSinceLastInterestAdded() > 0) {
                System.out.println("entro en el if pq a??os > 0");

                // new transaction to reflect the payment of interests.
                Transaction transaction = new Transaction(savAccount.getLastInterestGenerated());
                System.out.println("transaction amount" + transaction.getAmount().getAmount());
                transaction.setTransType(TransType.ANNUAL_INTERESTS);
                //transaction.setFromAccount(null);
                transaction.setToAccount((Account) account);
                transaction.setAuthorName("Ironbank");
                String dateString = interestAddedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                transaction.setDescription("Annual interests generated since " + dateString);
                transactionService.addTransaction(transaction);

                // update account info
                savAccount.updateInterestAddedDateTime();
            }
        }
        if (account instanceof CreditCard) {
            CreditCard credAccount = (CreditCard) account;

            LocalDateTime interestAddedDateTime = credAccount.getInterestAddedDateTime();
            LocalDateTime now = LocalDateTime.now();
            //long years = ChronoUnit.YEARS.between(getInterestAddedDateTime(), LocalDateTime.now());
           // if (credAccount.getYearsSinceLastInterestAdded() > 0) {
                if (ChronoUnit.YEARS.between(interestAddedDateTime,now) > 0) {

                // new transaction to reflect the payment of interests.
                Transaction transaction = new Transaction(credAccount.getLastInterestGenerated());
                transaction.setTransType(TransType.ANNUAL_INTERESTS);
                transaction.setToAccount((Account) account);
                transaction.setAuthorName("Ironbank");
                String dateString = interestAddedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                transaction.setDescription("Annual interests generated since " + dateString);
                transactionService.addTransaction(transaction);

                // update account info
                credAccount.updateInterestAddedDateTime();
            }
        }

    }

    public void applyMonthlyInterest(Account account) {
        if (account instanceof Savings ) {
            Savings savAccount = (Savings) account;
            LocalDateTime interestAddedDateTime = savAccount.getInterestAddedDateTime();
            if (savAccount.getYearsSinceLastInterestAdded() > 0) {

                // new transaction to reflect the payment of interests.
                Transaction transaction = new Transaction(savAccount.getLastInterestGenerated());
                transaction.setTransType(TransType.MONTHLY_INTERESTS);
                transaction.setToAccount((Account) account);
                transaction.setAuthorName("Ironbank");
                String dateString = interestAddedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                transaction.setDescription("Monthly interests generated since " + dateString);
                transactionService.addTransaction(transaction);

                // update account info
                savAccount.updateInterestAddedDateTime();
            }
        }
        if (account instanceof CreditCard ) {
            CreditCard credAccount = (CreditCard) account;

            LocalDateTime interestAddedDateTime = credAccount.getInterestAddedDateTime();
            if (credAccount.getYearsSinceLastInterestAdded() > 0) {

                // new transaction to reflect the payment of interests.
                Transaction transaction = new Transaction(credAccount.getLastInterestGenerated());
                transaction.setTransType(TransType.MONTHLY_INTERESTS);
                transaction.setToAccount((Account) account);
                transaction.setAuthorName("Ironbank");
                String dateString = interestAddedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                transaction.setDescription("Monthly interests generated since " + dateString);
                transactionService.addTransaction(transaction);

                // update account info
                credAccount.updateInterestAddedDateTime();
            }
        }

    }


}
