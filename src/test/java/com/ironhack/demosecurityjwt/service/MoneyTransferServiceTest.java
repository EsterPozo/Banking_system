package com.ironhack.demosecurityjwt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.demosecurityjwt.dtos.transaction.TransactionDTO;
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
import com.ironhack.demosecurityjwt.services.impl.account.AccountService;
import com.ironhack.demosecurityjwt.services.impl.transaction.MoneyTransferService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MoneyTransferServiceTest {


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
    private MoneyTransferService moneyTransferService;

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
        accountHolder2.setPassword("password2");

        ThirdParty thirdPartyUser = new ThirdParty("Google", "Hola");

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

        Transaction transaction = new Transaction(checkingAccount, studentCheckingAccount, new Money(BigDecimal.valueOf(100L)), "Alejandro Martínez Naredo", "Esto es una prueba");
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

    void doMoneyTransfer() {
        Account origin = accountRepository.findByPrimaryOwner(accountHolderRepository.findAll().get(0)).get(0);
        Long originId = origin.getId();
        Account destination = accountRepository.findByPrimaryOwner(accountHolderRepository.findAll().get(0)).get(1);
        Long destinationId = destination.getId();
        BigDecimal amount1 = transactionRepository.findAll().get(0).getAmount().getAmount();
        BigDecimal amount = BigDecimal.valueOf(200L);
        TransactionDTO moneyTransferDTO = new TransactionDTO();
        moneyTransferDTO.setAmount(amount1);
        moneyTransferDTO.setName("Alejandro Martínez");
        moneyTransferDTO.setDescription("money transfer test");
        moneyTransferDTO.setToAccountId(destinationId);
        moneyTransferDTO.setFromAccountId(originId);
        moneyTransferService.doMoneyTransfer(moneyTransferDTO);

        origin = accountRepository.findByPrimaryOwner(accountHolderRepository.findAll().get(0)).get(0);
        destination = accountRepository.findByPrimaryOwner(accountHolderRepository.findAll().get(0)).get(1);

        BigDecimal diff = destination.getBalance().getAmount().subtract(origin.getBalance().getAmount()).abs();

        assertEquals(BigDecimal.valueOf(200L).setScale(2, RoundingMode.HALF_EVEN), diff);
    }

}
