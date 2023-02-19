package com.ironhack.demosecurityjwt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.Address;
import com.ironhack.demosecurityjwt.models.user.ThirdParty;
import com.ironhack.demosecurityjwt.repositories.account.*;
import com.ironhack.demosecurityjwt.repositories.transaction.TransactionRepository;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.repositories.user.ThirdPartyRepository;
import com.ironhack.demosecurityjwt.repositories.user.UserRepository;
import com.ironhack.demosecurityjwt.services.impl.account.AccountService;
import com.ironhack.demosecurityjwt.services.impl.transaction.InterestFeesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class InterestFeesServiceTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountRepository accountRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
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
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestFeesService interestFeesService;

    Savings savingsAccount;

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
        StudentChecking studentCheckingAccount = new StudentChecking(new Money(BigDecimal.valueOf(1200L)),accountHolder,  "4321");
        savingsAccount = new Savings( new Money(BigDecimal.valueOf(1000L)), accountHolder, "1234");
        System.out.println("setter get interest de savingaccount" + savingsAccount.getInterestRate());
        CreditCard creditCardAccount = new CreditCard(new Money(BigDecimal.valueOf(1000L)), accountHolder);

        checkingRepository.save(checkingAccount);
        studentCheckingRepository.save(studentCheckingAccount);
        savingsRepository.save(savingsAccount);
        creditCardRepository.save(creditCardAccount);

    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        checkingRepository.deleteAll();
        studentCheckingRepository.deleteAll();
        creditCardRepository.deleteAll();
        savingsRepository.deleteAll();

        accountHolderRepository.deleteAll();

    }

    @Test
    void applyInterestsFeesService() {

        Checking checkingAccount = checkingRepository.findAll().get(0);
        checkingAccount.setMonthlyFeeAppliedDateTime(LocalDateTime.of(2023, 1, 14,0,0,0));
            interestFeesService.applyInterestsFeesService(checkingAccount);

        StudentChecking studentCheckingAccount = studentCheckingRepository.findAll().get(0);
        interestFeesService.applyInterestsFeesService(studentCheckingAccount);

        Savings savingsAccount1 = savingsRepository.findById(savingsAccount.getId()).get();
        savingsAccount1.setInterestAddedDateTime(LocalDateTime.of(2022, 1, 14,0,0,0));
        //interest rate is not saved properly - ZEROS ARE LOST!!
       // savingsAccount1.setInterestRate(new BigDecimal("0.0025"));

        System.out.println("interest rate savings acc2: " + savingsAccount1.getInterestRate());
        System.out.println("NAME : " + savingsAccount1.getPrimaryOwner().getName());
        interestFeesService.applyInterestsFeesService(savingsAccount1);

        CreditCard creditCardAccount = creditCardRepository.findAll().get(0);
        creditCardAccount.setInterestAddedDateTime(LocalDateTime.of(2022, 1, 14,0,0,0));
        interestFeesService.applyInterestsFeesService(creditCardAccount);

//not working because addtransaction is not working
        assertEquals(BigDecimal.valueOf(1000 - checkingAccount.getMonthlyMaintenanceFee().getAmount().intValue()).longValueExact(),
               checkingAccount.getBalance().getAmount().longValueExact());
        assertEquals(BigDecimal.valueOf(1200).longValueExact(), studentCheckingAccount.getBalance().getAmount().longValueExact());
       assertTrue(BigDecimal.valueOf(1000).compareTo(creditCardAccount.getBalance().getAmount()) < 0);
        assertEquals(BigDecimal.valueOf(1002.5).setScale(2), savingsAccount1.getBalance().getAmount());
    }
}
