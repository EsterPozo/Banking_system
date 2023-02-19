package com.ironhack.demosecurityjwt.service;

import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.Address;
import com.ironhack.demosecurityjwt.models.user.ThirdParty;
import com.ironhack.demosecurityjwt.repositories.account.*;
import com.ironhack.demosecurityjwt.repositories.transaction.TransactionRepository;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.repositories.user.ThirdPartyRepository;
import com.ironhack.demosecurityjwt.repositories.user.UserRepository;
import com.ironhack.demosecurityjwt.services.impl.transaction.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private SavingsRepository savingsRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;

    @BeforeEach
    void setUp() {

        AccountHolder accountHolder = new AccountHolder(
                "Alejandro Martinez", "username1", "password",
                LocalDate.of(1984, 4, 14),
                new Address("Calle Velázquez 1", "Gijón", "33201"));

        AccountHolder accountHolder2 = new AccountHolder(
                "Alba Pou", "username2", "password",
                LocalDate.of(1990, 4, 14),
                new Address("Calle Pizarro 1", "Barcelona", "08201"));

        ThirdParty thirdPartyUser = new ThirdParty("Google","username2","password", "elgooG");

        accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder2);
        thirdPartyRepository.save(thirdPartyUser);

        Checking checkingAccount = new Checking(new Money(BigDecimal.valueOf(1000L)),accountHolder,  "1234");
        StudentChecking studentCheckingAccount = new StudentChecking(new Money(BigDecimal.valueOf(1200L)),accountHolder2,  "4321");
        Savings savingsAccount = new Savings( new Money(BigDecimal.valueOf(1000L)), accountHolder, "1234");
        CreditCard creditCardAccount = new CreditCard(new Money(BigDecimal.valueOf(1000L)), accountHolder);
        checkingRepository.save(checkingAccount);
        studentCheckingRepository.save(studentCheckingAccount);
        savingsRepository.save(savingsAccount);
      creditCardRepository.save(creditCardAccount);

        Money amount = new Money(BigDecimal.valueOf(100L));
        Transaction transaction = new Transaction(checkingAccount, studentCheckingAccount, amount, "Alejandro Martínez Naredo", "Esto es una prueba");
        transactionRepository.save(transaction);
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        //userRepository.deleteAll();
        checkingRepository.deleteAll();
        studentCheckingRepository.deleteAll();
        creditCardRepository.deleteAll();
        savingsRepository.deleteAll();

        accountHolderRepository.deleteAll();

    }
    @Test
    void getTransactions() {
        Collection<Transaction> transactions = transactionService.getTransactions();
        assertEquals(1, transactions.size());
    }

    @Test
    void addTransaction() {
        List<AccountHolder> owners = accountHolderRepository.findAll();
        Account fromAccount = owners.get(0).getPrimaryAccounts().get(0);
        Account toAccount = owners.get(1).getPrimaryAccounts().get(0);

        BigDecimal fromBalance = fromAccount.getBalance().getAmount();
        BigDecimal toBalance = toAccount.getBalance().getAmount();

        Transaction transaction = new Transaction(fromAccount, toAccount, new Money(BigDecimal.valueOf(100L)), "Alejandro", "Hola amigo");
        transactionService.addTransaction(transaction);
        assertEquals(2, transactionService.getTransactions().size());

       assertEquals(fromBalance.intValue() -100, fromAccount.getBalance().getAmount().intValue());
        assertEquals(toBalance.intValue() + 100, toAccount.getBalance().getAmount().intValue());
    }

}
