package com.ironhack.demosecurityjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.Address;
import com.ironhack.demosecurityjwt.models.user.Admin;
import com.ironhack.demosecurityjwt.models.user.ThirdParty;
import com.ironhack.demosecurityjwt.repositories.account.*;
import com.ironhack.demosecurityjwt.repositories.transaction.TransactionRepository;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.repositories.user.AdminRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AccountControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private SavingsRepository savingsRepository;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        AccountHolder accountHolder = new AccountHolder(
                "Mr. Account Holder",
                LocalDate.of(1990, 4, 14),
                new Address("Street", "City", "PostalCode"));
        accountHolder.setUsername("mister");
        accountHolder.setPassword("mister");
//        ah.setRoles(new HashSet<Role>(Collections.singletonList(new Role("OWNER", ah))));
        AccountHolder accountHolder2 = new AccountHolder(
                "Mrs. Account Holder",
                LocalDate.of(2000, 4, 14),
                new Address("Street", "City", "PostalCode"));
        accountHolder2.setUsername("mistress");
        accountHolder2.setPassword("mistress");
//        ah2.setRoles(new HashSet<Role>(Collections.singletonList(new Role("OWNER", ah2))));
        ThirdParty tpu = new ThirdParty(
                "Third Party User",
                "hashedKey");
        tpu.setUsername("thirdpartyuser");
        tpu.setPassword("thirdpartyuser");
//        tpu.setRoles(new HashSet<Role>(Collections.singletonList(new Role("OWNER", tpu))));

        accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder2);

//        Checking check = new Checking(
//                tpu,
//                new Money(BigDecimal.valueOf(10000L)),
//                "43211234");
//        StudentChecking stCheck = new StudentChecking(
//                ah2,
//                new Money(BigDecimal.valueOf(5000L)),
//                "12345678");
//        Savings sav = new Savings(
//                ah,
//                new Money(BigDecimal.valueOf(10000L)),
//                "98765432");
//        sav.setSecondaryOwner(ah2);
//        CreditCard cc = new CreditCard(
//                ah,
//                new Money(BigDecimal.valueOf(1000L)));
//
//        repository.saveAll(List.of(check, stCheck, sav, cc));

        Checking checkingAccount = new Checking(new Money(BigDecimal.valueOf(1000L)),accountHolder,  "1234");
        StudentChecking studentCheckingAccount = new StudentChecking(new Money(BigDecimal.valueOf(1200L)),accountHolder2,  "4321");
        Savings savingsAccount = new Savings( new Money(BigDecimal.valueOf(1000L)), accountHolder, "1234");
        CreditCard creditCardAccount = new CreditCard(new Money(BigDecimal.valueOf(1000L)), accountHolder);


        checkingRepository.save(checkingAccount);
        studentCheckingRepository.save(studentCheckingAccount);
        savingsRepository.save(savingsAccount);
        creditCardRepository.save(creditCardAccount);

        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("ironhack");
        adminRepository.save(admin);
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

    void getAccounts() throws Exception {

        MvcResult result = mockMvc.perform(
                        get("/bank/accounts"))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Mr. Account Holder"));
       assertTrue(result.getResponse().getContentAsString().contains("Mrs. Account Holder"));

    }

    @Test

    void getAccount() throws Exception {

        List<Account> accounts = accountRepository.findAll();
        Checking acc1 = checkingRepository.findAll().get(0);
        MvcResult result = mockMvc.perform(
                        get("/bank/accounts/" + acc1.getId()))
                .andExpect(status().isOk())
                .andReturn();
       // assertTrue(result.getResponse().getContentAsString().contains(accounts.get(0).getPrimaryOwner().getName()));
        assertTrue(result.getResponse().getContentAsString().contains(acc1.getPrimaryOwner().getName()));
    }

    @Test
    @WithMockUser(username = "admin", password = "ironhack")
    void getAccountsByOwner() throws Exception {

        List<Account> accounts = accountRepository.findAll();
        List<AccountHolder> owners = accountHolderRepository.findAll();
        Long idOwner = accounts.get(2).getPrimaryOwner().getId();
        MvcResult result = mockMvc.perform(
                        get("/bank/users/owners/" + idOwner + "/accounts"))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(owners.get(0).getName()));
       // assertTrue(result.getResponse().getContentAsString().contains(owners.get(1).getName()));
    }

}
