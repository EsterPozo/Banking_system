package com.ironhack.demosecurityjwt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.demosecurityjwt.dtos.account.AccountDTO;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
public class AccountServiceTest {

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

    @BeforeEach
    void setUp() {


        AccountHolder accountHolder = new AccountHolder(
                "Alejandro Martinez",
                LocalDate.of(1984, 4, 14),
                new Address("Calle Velázquez 1", "Gijón", "33201"));
        accountHolder.setUsername("username1");
        accountHolder.setPassword("password");

//        ThirdParty thirdPartyUser = new ThirdParty("Google", "elgooG");
//        thirdPartyUser.setUsername("username2");
//        thirdPartyUser.setPassword("password");

   accountHolderRepository.save(accountHolder);
//       // thirdPartyRepository.save(thirdPartyUser);
//
        Checking checkingAccount = new Checking(new Money(BigDecimal.valueOf(1000L)),accountHolder,  "1234");
        StudentChecking studentCheckingAccount = new StudentChecking(new Money(BigDecimal.valueOf(1000L)),accountHolder,  "4321");
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
    void getAccounts() {
        List<Account> accountList = accountService.getAccounts();
        assertEquals(4, accountList.size());
    }

    @Test
    void existsAccountById() {
        Account account = checkingRepository.findByPrimaryOwnerName("Alejandro Martinez");
        //checkingRepository.save(account);
        Long accountId = account.getId();

        assertTrue(accountService.existsAccount(accountId));
        assertTrue(accountService.existsAccount(accountId + 1));
       assertFalse(accountService.existsAccount(accountId - 1));
    }

    @Test
    void getAccountById() {

        Account account = accountRepository.findByPrimaryOwner(accountHolderRepository.findAll().get(0)).get(0);
        Long accountId = account.getId();

        Account accountFound = accountService.getAccountById(accountId);
        assertTrue(accountFound.getPrimaryOwner().getName().equalsIgnoreCase("Alejandro Martinez"));
    }

    @Test
    void addAccount() {
        AccountHolder owner = new AccountHolder(
                "Pedro Perez",
                LocalDate.of(1959, 5, 6),
                new Address("Calle Dos", "Madrid", "28080"));
        accountHolderRepository.save(owner);
        Checking account = new Checking( new Money(BigDecimal.ZERO) ,owner, "1234");

        accountService.addAccount(account);

        assertEquals(5, accountService.getAccounts().size());
    }

    @Test
    void addChecking() {
        AccountHolder owner = new AccountHolder(
                "Pedro Perez",
                LocalDate.of(1959, 5, 6),
                new Address("Calle Dos", "Madrid", "28080"));
        AccountHolder primaryOwner = accountHolderRepository.save(owner);

        AccountDTO checkingAccountDTO = new AccountDTO();
        checkingAccountDTO.setBalance(BigDecimal.ZERO);
        checkingAccountDTO.setSecretKey("secretkey");
        checkingAccountDTO.setOwnerId(accountHolderRepository.findByName("Pedro Perez").getId());
        System.out.println("This is owner id " + checkingAccountDTO.getOwnerId());
        System.out.println("This is owner sec " + checkingAccountDTO.getSecretKey());
        accountService.addChecking(checkingAccountDTO);

        List<Account> accounts = accountRepository.findByPrimaryOwner(primaryOwner);
        assertEquals(1, accounts.size());
       assertEquals("secretkey", ((Checking)accounts.get(accounts.size()-1)).getSecretKey());
    }

    @Test
    void addChecking_ownerUnder24() {
        AccountHolder owner = new AccountHolder(
                "Jaime Lopez",
                LocalDate.of(2005, 5, 6),
                new Address("Calle Dos", "Madrid", "28080"));
        AccountHolder primaryOwner = accountHolderRepository.save(owner);

        AccountDTO checkingAccountDTO = new AccountDTO();
        checkingAccountDTO.setBalance(BigDecimal.ZERO);
        checkingAccountDTO.setSecretKey("secretkey");
        checkingAccountDTO.setOwnerId(accountHolderRepository.findByName("Jaime Lopez").getId());

        accountService.addChecking(checkingAccountDTO);

        //List<Account> accounts = accountRepository.findByPrimaryOwnerName("Pedro Perez");
        List<Account> accounts = accountRepository.findByPrimaryOwner(primaryOwner);
        assertEquals(1, accounts.size());
        assertEquals("secretkey", ((StudentChecking)accounts.get(accounts.size()-1)).getSecretKey());
        assertTrue(accounts.get(0) instanceof StudentChecking);

    }

}
