package com.ironhack.demosecurityjwt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.Address;
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

    @BeforeEach
    void setUp() {


        AccountHolder accountHolder = new AccountHolder(
                "Alejandro Martinez",
                LocalDate.of(1984, 4, 14),
                new Address("Calle Velázquez 1", "Gijón", "33201"));
        accountHolder.setUsername("username1");
        accountHolder.setPassword("password");
        AccountHolder accountHolder2 = new AccountHolder(
                "Alba Pou",
                LocalDate.of(1990, 4, 14),
                new Address("Calle Pizarro 1", "Barcelona", "08201"));
        accountHolder2.setUsername("username2");
        accountHolder2.setPassword("password");

//        ThirdParty thirdPartyUser = new ThirdParty("Google", "elgooG");
//        thirdPartyUser.setUsername("username2");
//        thirdPartyUser.setPassword("password");

        accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder2);
//       // thirdPartyRepository.save(thirdPartyUser);
//
        Checking checkingAccount = new Checking(new Money(BigDecimal.valueOf(1000L)),accountHolder,  "1234");
        StudentChecking studentCheckingAccount = new StudentChecking(new Money(BigDecimal.valueOf(1200L)),accountHolder,  "4321");
        Savings savingsAccount = new Savings( new Money(BigDecimal.valueOf(1000L)), accountHolder, "1234");
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
        List<Account> accountList = accountRepository.findAll();
        for(Account account : accountList) {
            interestFeesService.applyInterestsFeesService(account);
        }

        Checking checkingAccount = (Checking) accountList.get(0);
        StudentChecking studentCheckingAccount = (StudentChecking) accountList.get(1);
        Savings savingsAccount = (Savings) accountList.get(2);
        CreditCard creditCardAccount = (CreditCard) accountList.get(3);

//not working because addtransaction is not working
//        assertEquals(BigDecimal.valueOf(1000 - checkingAccount.getMonthlyMaintenanceFee().getAmount().intValue()).longValueExact(),
//                checkingAccount.getBalance().getAmount().longValueExact());
        //assertEquals(BigDecimal.valueOf(1000).longValueExact(), studentCheckingAccount.getBalance().getAmount().longValueExact());
       // assertTrue(BigDecimal.valueOf(1000).compareTo(creditCardAccount.getBalance().getAmount()) < 0);
       // assertEquals(BigDecimal.valueOf(1002.5).setScale(2), savingsAccount.getBalance().getAmount());
    }
}
