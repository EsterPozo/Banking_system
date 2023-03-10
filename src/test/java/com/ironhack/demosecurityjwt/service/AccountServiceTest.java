package com.ironhack.demosecurityjwt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.demosecurityjwt.dtos.account.AccountDTO;
import com.ironhack.demosecurityjwt.dtos.account.NewBalanceDTO;
import com.ironhack.demosecurityjwt.dtos.transaction.TransactionDTO;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.Address;
import com.ironhack.demosecurityjwt.models.user.ThirdParty;
import com.ironhack.demosecurityjwt.models.user.User;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {


        AccountHolder accountHolder = new AccountHolder(
                "Alejandro Martinez", "username1", "password",
                LocalDate.of(1984, 4, 14),
                new Address("Calle Vel??zquez 1", "Gij??n", "33201"));

        AccountHolder accountHolder2 = new AccountHolder(
                "Alba Pou", "username2", "password",
                LocalDate.of(1990, 4, 14),
                new Address("Calle Pizarro 1", "Barcelona", "08201"));

        ThirdParty thirdPartyUser = new ThirdParty("Google", "elgooG");
        thirdPartyUser.setUsername("usernametpu");
        thirdPartyUser.setPassword("password-tpu");

   accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder2);
     thirdPartyRepository.save(thirdPartyUser);

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

    @Test
    void addSavings() {
        AccountHolder primaryOwner = accountHolderRepository.findAll().get(0);
        AccountHolder owner = new AccountHolder(
                "Pedro Perez",
                LocalDate.of(1959, 5, 6),
                new Address("Calle Dos", "Madrid", "28080"));
        AccountHolder secondaryOwner = accountHolderRepository.save(owner);

        AccountDTO savingsAccountDTO = new AccountDTO();
        savingsAccountDTO.setBalance(BigDecimal.valueOf(1000L));
        savingsAccountDTO.setSecretKey("keysecret");
        savingsAccountDTO.setInterestRate(BigDecimal.valueOf(0.0025));
        savingsAccountDTO.setMinBalance(BigDecimal.valueOf(500));
        savingsAccountDTO.setOwnerId(accountHolderRepository.findByName("Alejandro Martinez").getId());
        savingsAccountDTO.setOtherOwnerId(accountHolderRepository.findByName("Pedro Perez").getId());
        accountService.addSavings(savingsAccountDTO);

        List<Account> accounts = accountRepository.findByPrimaryOwner(primaryOwner);
        List<Account> moreAccounts = accountRepository.findBySecondaryOwner(secondaryOwner);

        assertEquals(5, accounts.size());
        assertEquals(1, moreAccounts.size());

        assertEquals("keysecret", ((Savings)accounts.get(accounts.size()-1)).getSecretKey());
        assertEquals("keysecret", ((Savings)moreAccounts.get(0)).getSecretKey());
        assertEquals(secondaryOwner.getName(), accounts.get(accounts.size()-1).getSecondaryOwner().getName());
        assertEquals(primaryOwner.getName(), moreAccounts.get(0).getPrimaryOwner().getName());

    }

    @Test
    void addCreditCard() {
        AccountHolder primaryOwner = accountHolderRepository.findAll().get(1);
        AccountHolder secondaryOwner = accountHolderRepository.findAll().get(0);

        AccountDTO creditCardAccountDTO = new AccountDTO();
        creditCardAccountDTO.setBalance(BigDecimal.valueOf(1234L));
        creditCardAccountDTO.setInterestRate(BigDecimal.valueOf(0.125));
        creditCardAccountDTO.setCreditLimit(BigDecimal.valueOf(2121));
        creditCardAccountDTO.setOwnerId(primaryOwner.getId());
        creditCardAccountDTO.setOtherOwnerId(secondaryOwner.getId());
        accountService.addCreditCard(creditCardAccountDTO);

        List<Account> accounts = accountRepository.findByPrimaryOwner(primaryOwner);
        List<Account> moreAccounts = accountRepository.findBySecondaryOwner(secondaryOwner);

        assertEquals(1, accounts.size());
        assertEquals(1, moreAccounts.size());

        assertEquals(new BigDecimal("1234.00"), accounts.get(accounts.size()-1).getBalance().getAmount());
        assertEquals(new BigDecimal("2121.00"), ((CreditCard)moreAccounts.get(0)).getCreditLimit().getAmount());
        assertEquals(secondaryOwner.getName(), accounts.get(accounts.size()-1).getSecondaryOwner().getName());

    }

    @Test
    void updateBalance() {
        NewBalanceDTO newBalanceDTO = new NewBalanceDTO();
        newBalanceDTO.setBalance(BigDecimal.valueOf(12345.67));

        accountService.updateBalance(newBalanceDTO, accountService.getAccounts().get(0).getId());
        assertEquals(BigDecimal.valueOf(12345.67), accountService.getAccounts().get(0).getBalance().getAmount());

    }

    @Test
    void startMoneyTransfer() {

        Account origin = accountRepository.findByPrimaryOwner(accountHolderRepository.findAll().get(0)).get(0);
        Long originId = origin.getId();
        Account destination = accountRepository.findByPrimaryOwner(accountHolderRepository.findAll().get(0)).get(1);
        Long destinationId = destination.getId();

        BigDecimal amount = BigDecimal.valueOf(90L);
        TransactionDTO moneyTransferDTO = new TransactionDTO();
        moneyTransferDTO.setAmount(amount);
        moneyTransferDTO.setName("Alejandro Martinez");
        moneyTransferDTO.setDescription("money transfer test");
        moneyTransferDTO.setToAccountId(destinationId);
        moneyTransferDTO.setFromAccountId(originId);

        AccountHolder ac = accountHolderRepository.findByName("Alejandro Martinez");
        UserDetails userDetails = (UserDetails) ac;

        System.out.println("user detals username" + userDetails.getUsername());

        accountService.startMoneyTransfer(userDetails, moneyTransferDTO);

       origin = accountRepository.findByPrimaryOwner(accountHolderRepository.findAll().get(0)).get(0);
       destination = accountRepository.findByPrimaryOwner(accountHolderRepository.findAll().get(0)).get(1);

        BigDecimal diff = destination.getBalance().getAmount().subtract(origin.getBalance().getAmount()).abs();
        //assertEquals(BigDecimal.valueOf(200L).setScale(2, RoundingMode.HALF_EVEN), diff);
    }

}
