package com.ironhack.demosecurityjwt.services.impl.account;

import com.ironhack.demosecurityjwt.dtos.account.*;
import com.ironhack.demosecurityjwt.dtos.transaction.TransactionDTO;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import com.ironhack.demosecurityjwt.models.transaction.enums.TransType;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.User;
import com.ironhack.demosecurityjwt.repositories.account.*;
import com.ironhack.demosecurityjwt.repositories.transaction.TransactionRepository;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.repositories.user.UserRepository;

import com.ironhack.demosecurityjwt.services.interfaces.IAccountService;
import com.ironhack.demosecurityjwt.services.interfaces.IMoneyTransferService;
import com.ironhack.demosecurityjwt.services.interfaces.ITransactionService;
import com.ironhack.demosecurityjwt.services.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private SavingsRepository savingsRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private IMoneyTransferService moneyTransferService;

//    @Autowired
//    private ITransactionService transactionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        if (!accountRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        return accountRepository.findById(id).get();
    }

    //public Account getAccountByIdWithAuth()
    public Account getAccountByIdWithAuth(UserDetails userDetails, Long id) {
        if (!accountRepository.existsById(id))
            throw new ResponseStatusException(NOT_FOUND, "Account id not found");

        //authorize access
        if (!authService.authAccountAccess(userDetails,id))
            throw new ResponseStatusException(UNAUTHORIZED, "Access denied");

        //get the account
        Account account = accountRepository.findById(id).get();

        // apply interest
        // update last time accessed

        return accountRepository.save(account);
    }

    public List<Account> getAccountsByOwner(Long idOwner) {
        if (!accountHolderRepository.existsById(idOwner))
            throw new ResponseStatusException(BAD_REQUEST, "Id not valid");

        AccountHolder owner = accountHolderRepository.findById(idOwner).get();
        return accountRepository.findByPrimaryOwnerOrSecondaryOwner(owner, owner);
    }

    public Account addAccount(Account account) {
        if (account instanceof Checking) {
            return checkingRepository.save((Checking) account);
        } else if (account instanceof StudentChecking) {
            return studentCheckingRepository.save((StudentChecking) account);
        } else if( account instanceof Savings) {
            return savingsRepository.save((Savings) account);
        } else if (account instanceof CreditCard) {
            return creditCardRepository.save((CreditCard) account);
        } else {
            throw new ResponseStatusException(BAD_REQUEST, "The account type does not exist");
        }
    }

    public Account addChecking(AccountDTO accountDTO) {
        if (!accountHolderRepository.existsById(accountDTO.getOwnerId()) || !accountHolderRepository.existsById(accountDTO.getOtherOwnerId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Id not valid");
        }
            AccountHolder owner = accountHolderRepository.findById(accountDTO.getOwnerId()).get();
            AccountHolder otherOwner = accountHolderRepository.findById(accountDTO.getOtherOwnerId()).get();

            LocalDate today = LocalDate.now();
            int age = owner.getDateOfBirth().until(today).getYears();

            if (age < 24) {
                StudentChecking studentChecking = new StudentChecking();
                studentChecking.setPrimaryOwner(owner);
                studentChecking.setSecondaryOwner(otherOwner);
                studentChecking.setBalance(new Money(accountDTO.getBalance()));
                studentChecking.setSecretKey(accountDTO.getSecretKey());

                return studentCheckingRepository.save(studentChecking);
            }

            Checking checking = new Checking();
            checking.setPrimaryOwner(owner);
            checking.setSecondaryOwner(otherOwner);
            checking.setBalance(new Money(accountDTO.getBalance()));
            checking.setMinimumBalance(new Money(accountDTO.getMinBalance()));
            checking.setSecretKey(accountDTO.getSecretKey());

            return checkingRepository.save(checking);

        }


        public Account addSavings(AccountDTO accountDTO) {
        if (!accountHolderRepository.existsById(accountDTO.getOwnerId()) || !accountHolderRepository.existsById(accountDTO.getOtherOwnerId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Id not valid");
        }

        AccountHolder owner = accountHolderRepository.findById(accountDTO.getOwnerId()).get();
        AccountHolder otherOwner = accountHolderRepository.findById(accountDTO.getOtherOwnerId()).get();

        Savings savings = new Savings();
        savings.setPrimaryOwner(owner);
        savings.setSecondaryOwner(otherOwner);
        savings.setBalance(new Money(accountDTO.getBalance()));
        savings.setSecretKey(accountDTO.getSecretKey());
        savings.setInterestRate(accountDTO.getInterestRate());
        savings.setMinimumBalance(new Money(accountDTO.getMinBalance()));

        return savingsRepository.save(savings);
    }

    public Account addCreditCard(AccountDTO accountDTO) {
        if (!accountHolderRepository.existsById(accountDTO.getOwnerId()) || !accountHolderRepository.existsById(accountDTO.getOtherOwnerId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Id not valid");
        }
        AccountHolder owner = accountHolderRepository.findById(accountDTO.getOwnerId()).get();
        AccountHolder otherOwner = accountHolderRepository.findById(accountDTO.getOtherOwnerId()).get();

        CreditCard creditCard = new CreditCard();
        creditCard.setPrimaryOwner(owner);
        creditCard.setSecondaryOwner(otherOwner);
        creditCard.setBalance(new Money(accountDTO.getBalance()));
        creditCard.setInterestRate(accountDTO.getInterestRate());

        return creditCardRepository.save(creditCard);

    }
    public Boolean existsAccount(Long id) {

        return accountRepository.existsById(id);
    }
    public void updateBalance(NewBalanceDTO newBalanceDTO, Long id) {
        if(!existsAccount(id))
            throw new ResponseStatusException(BAD_REQUEST, "Account id not found");

        Account account = accountRepository.findById(id).get();
        account.setBalance(new Money(newBalanceDTO.getBalance()));
        accountRepository.save(account);

    }

    public Account startMoneyTransfer(UserDetails userDetails, TransactionDTO transactionDTO) {
        // check the existance of accounts
        if(!existsAccount(transactionDTO.getFromAccountId()))
            throw new ResponseStatusException(BAD_REQUEST, "Origin account not found");
        if(!existsAccount(transactionDTO.getToAccountId()))
            throw new ResponseStatusException(BAD_REQUEST, "Destination account not found");
        // check the accounts are not the same
        if(transactionDTO.getFromAccountId().equals(transactionDTO.getToAccountId()))
            throw new ResponseStatusException(BAD_REQUEST, "Origin and destination accounts are the same");

        Account account = accountRepository.findById(transactionDTO.getFromAccountId()).get();

        //check the person is he authenticated for this account?¿?
        String primaryName = account.getPrimaryOwner().getName();
        String secondaryName = account.getSecondaryOwner().getName();
        String transferName = transactionDTO.getName();
        User user = userRepository.findByUsername(userDetails.getUsername());
        String authName = user.getName();

        if(!transferName.equalsIgnoreCase(primaryName) && !transferName.equalsIgnoreCase(secondaryName))
            throw new ResponseStatusException(BAD_REQUEST, "Transaction author name does not math with the origin account owner");
        //must be a better way considering userdetails..

        // everything ok, next checks are doMoneyTransfer responsibility

        account = moneyTransferService.doMoneyTransfer(transactionDTO);

        return accountRepository.save(account);
    }

//    public Account doMoneyTransfer(TransactionDTO transactionDTO) {
//
//        //get accounts involved
//        Account origin = getAccountById(transactionDTO.getFromAccountId());
//        Account destination = getAccountById(transactionDTO.getToAccountId());
//
//        //check fraud detection
//
//        //check enough funds in origin
//        BigDecimal currentBalance = origin.getBalance().getAmount();
//        BigDecimal transferAmount = transactionDTO.getAmount();
//        if(transferAmount.compareTo(currentBalance) > 0)
//            throw new ResponseStatusException(BAD_REQUEST, "Amount exceeds balance of account");
//
//        //check if penalty fee has to be deduced later
//        BigDecimal result = currentBalance.subtract(transferAmount);
//        boolean applyPenaltyFee = currentBalance.compareTo(origin.getMinimumBalance().getAmount()) >0 && result.compareTo(origin.getMinimumBalance().getAmount()) < 0;
//
//        // make transaction
//        Transaction transaction = new Transaction(new Money(transferAmount));
//        transaction.setTransType(TransType.MONEY_TRANSFER);
//        transaction.setFromAccount(origin);
//        transaction.setToAccount(destination);
//        transaction.setAuthorName(transactionDTO.getName());
//        transaction.setDescription(transactionDTO.getDescription());
//        transactionService.addTransaction(transaction);
//
//        //make penalty fee transaction
//        if(applyPenaltyFee) {
//            Transaction deductionTransaction = new Transaction(origin.getPenaltyFee());
//            transaction.setTransType(TransType.PENALTY_FEE);
//            deductionTransaction.setFromAccount(origin);
//            deductionTransaction.setAuthorName(transactionDTO.getName());
//            deductionTransaction.setDescription("Penalty fee deduction");
//           transactionService.addTransaction(deductionTransaction);
//        }

//        return accountRepository.save(origin);
//
//    }

//    public Account applyInterestsFeesService(Account account) {
//        if (account instanceof Checking) {
//            Checking checkAccount = (Checking) account;
//            applyMonthlyFee(checkAccount);
//            return addAccount(checkAccount);
//        } else if (account instanceof Savings) {
//            Savings savAccount = (Savings) account;
//            applyAnnualInterest(savAccount);
//            return addAccount(savAccount);
//        } else if(account instanceof CreditCard) {
//            CreditCard credAccount = (CreditCard) account;
//            applyAnnualInterest(credAccount);
//            return addAccount(credAccount);
//        }else if(account instanceof StudentChecking) {
//            StudentChecking studAccount = (StudentChecking) account;
//            return addAccount(studAccount);
//        } else {
//            throw new ResponseStatusException(BAD_REQUEST, "The account type does not exist");
//        }
//
//
//    }
//    public void applyMonthlyFee(Checking account) {
//        LocalDateTime feeAppliedDateTime = account.getMonthlyFeeAppliedDateTime();
//
//        if (account.getMonthsSinceLastMonthlyFeeDeduction() > 0) {
//            //new transaction to reflect the deduction of fees
//
//            Transaction transaction = new Transaction(account.getMonthlyMaintenanceFee());
//            transaction.setTransType(TransType.MAINTENANCE_FEE);
//            transaction.setFromAccount(account);
//            transaction.setAuthorName("Ironbank");
//            String dateString = feeAppliedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//            transaction.setDescription("Maintenance fee deductions since " + dateString);
//            transactionService.addTransaction(transaction);
//
//            //update account info
//            account.updateMonthlyFeeAppliedDateTime();
//        }
//    }
//    public void applyAnnualInterest(Account account) {
//        if (account instanceof Savings ) {
//            Savings savAccount = (Savings) account;
//            LocalDateTime interestAddedDateTime = savAccount.getInterestAddedDateTime();
//            if (savAccount.getYearsSinceLastInterestAdded() > 0) {
//
//                // new transaction to reflect the payment of interests.
//                Transaction transaction = new Transaction(savAccount.getLastInterestGenerated());
//                transaction.setTransType(TransType.ANNUAL_INTERESTS);
//                //transaction.setFromAccount(null);
//                transaction.setToAccount((Account) account);
//                transaction.setAuthorName("Ironbank");
//                String dateString = interestAddedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//                transaction.setDescription("Annual interests generated since " + dateString);
//                transactionService.addTransaction(transaction);
//
//                // update account info
//                savAccount.updateInterestAddedDateTime();
//            }
//        }
//        if (account instanceof CreditCard ) {
//            CreditCard credAccount = (CreditCard) account;
//
//            LocalDateTime interestAddedDateTime = credAccount.getInterestAddedDateTime();
//            if (credAccount.getYearsSinceLastInterestAdded() > 0) {
//
//                // new transaction to reflect the payment of interests.
//                Transaction transaction = new Transaction(credAccount.getLastInterestGenerated());
//                transaction.setTransType(TransType.ANNUAL_INTERESTS);
//                transaction.setToAccount((Account) account);
//                transaction.setAuthorName("Ironbank");
//                String dateString = interestAddedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//                transaction.setDescription("Annual interests generated since " + dateString);
//                transactionService.addTransaction(transaction);
//
//                // update account info
//                credAccount.updateInterestAddedDateTime();
//            }
//        }
//
//    }

//    public void applyMonthlyInterest(Account account) {
//        if (account instanceof Savings ) {
//            Savings savAccount = (Savings) account;
//            LocalDateTime interestAddedDateTime = savAccount.getInterestAddedDateTime();
//            if (savAccount.getYearsSinceLastInterestAdded() > 0) {
//
//                // new transaction to reflect the payment of interests.
//                Transaction transaction = new Transaction(savAccount.getLastInterestGenerated());
//                transaction.setTransType(TransType.MONTHLY_INTERESTS);
//                transaction.setToAccount((Account) account);
//                transaction.setAuthorName("Ironbank");
//                String dateString = interestAddedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//                transaction.setDescription("Monthly interests generated since " + dateString);
//                transactionService.addTransaction(transaction);
//
//                // update account info
//                savAccount.updateInterestAddedDateTime();
//            }
//        }
//        if (account instanceof CreditCard ) {
//            CreditCard credAccount = (CreditCard) account;
//
//            LocalDateTime interestAddedDateTime = credAccount.getInterestAddedDateTime();
//            if (credAccount.getYearsSinceLastInterestAdded() > 0) {
//
//                // new transaction to reflect the payment of interests.
//                Transaction transaction = new Transaction(credAccount.getLastInterestGenerated());
//                transaction.setTransType(TransType.MONTHLY_INTERESTS);
//                transaction.setToAccount((Account) account);
//                transaction.setAuthorName("Ironbank");
//                String dateString = interestAddedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//                transaction.setDescription("Monthly interests generated since " + dateString);
//                transactionService.addTransaction(transaction);
//
//                // update account info
//                credAccount.updateInterestAddedDateTime();
//            }
//        }
//
//    }

}
