package com.ironhack.demosecurityjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.transaction.Transaction;
import com.ironhack.demosecurityjwt.models.transaction.enums.TransType;
import com.ironhack.demosecurityjwt.models.user.*;
import com.ironhack.demosecurityjwt.repositories.account.*;
import com.ironhack.demosecurityjwt.repositories.transaction.TransactionRepository;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.repositories.user.AdminRepository;
import com.ironhack.demosecurityjwt.repositories.user.ThirdPartyRepository;
import com.ironhack.demosecurityjwt.services.impl.transaction.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TransactionControllerTest {

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
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private TransactionService transactionService;


    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        AccountHolder accountHolder = new AccountHolder(
                "Mr. Account Holder", "mister", "password1",
                LocalDate.of(1990, 4, 14),
                new Address("Calle Velázquez 1", "Gijón", "33201"));



        AccountHolder accountHolder2 = new AccountHolder(
                "Mrs. Account Holder", "mistress", "password",
                LocalDate.of(2000, 4, 14),
                new Address("Calle Pizarro 1", "Barcelona", "08201"));


        ThirdParty tpu = new ThirdParty("Third Party User","thirdpartyuser","thirdpartyuser", "hashedKey");
        accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder2);
        thirdPartyRepository.save(tpu);

        Checking checkingAccount = new Checking(new Money(BigDecimal.valueOf(1000L)),accountHolder,  "1234");

        StudentChecking studentCheckingAccount = new StudentChecking(new Money(BigDecimal.valueOf(1200L)),accountHolder2,  "4321");
        Savings savingsAccount = new Savings( new Money(BigDecimal.valueOf(1000L)), accountHolder, "1234");
        savingsAccount.setSecondaryOwner(accountHolder2);
        CreditCard creditCardAccount = new CreditCard(new Money(BigDecimal.valueOf(1000L)), accountHolder2);


        checkingRepository.save(checkingAccount);
        studentCheckingRepository.save(studentCheckingAccount);
        savingsRepository.save(savingsAccount);
        creditCardRepository.save(creditCardAccount);

        Transaction tx1 = new Transaction(new Money (BigDecimal.valueOf(200L)));
        tx1.setTransType(TransType.MONEY_TRANSFER);
        tx1.setFromAccount(savingsAccount);
        tx1.setToAccount(creditCardAccount);
        tx1.setAuthorName("Mister");
        tx1.setDescription("Money transfer");
        tx1.setTimestamp(LocalDateTime.now().minusDays(1));
        transactionService.addTransaction(tx1);

        Transaction tx2 = new Transaction(new Money (BigDecimal.valueOf(200L)));
        tx2.setTransType(TransType.MONEY_TRANSFER);
        tx2.setFromAccount(savingsAccount);
        tx2.setToAccount(creditCardAccount);
        tx2.setAuthorName("Mister");
        tx2.setDescription("Another money transfer :D");
        tx2.setTimestamp(LocalDateTime.now());
        transactionService.addTransaction(tx2);

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
        transactionRepository.deleteAll();

        accountHolderRepository.deleteAll();

    }

   // @WithMockUser(username = "admin", password = "ironhack", roles = {"ADMIN"})
    @Test
    void getTransactions() throws Exception {
        MvcResult result =
                mockMvc.perform(
                                get("/bank/transactions"))
                        .andExpect(status().isOk())
                        .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Another"));
        assertTrue(result.getResponse().getContentAsString().contains("Money"));
        assertTrue(result.getResponse().getContentAsString().contains("transfer :D"));
    }

    @Test
   // @WithMockUser(username = "mister", password = "mister", roles = {"OWNER"})
//    @WithUserDetails(value = "mister"/*, userDetailsServiceBeanName = "userDetailsService"*/)
    void getTransactionsByAccount() throws Exception {
        Account account = accountRepository.findAll().get(2);
        System.out.println("account ID: " + account.getId());

        User user = account.getPrimaryOwner();
        UserDetails customUserDetails =(UserDetails) user;
    System.out.println(customUserDetails);
        System.out.println("CUSTOMER DETAILS USERNAME: " + customUserDetails.getUsername());

        MvcResult result =
                mockMvc.perform(
                                get("/accounts/" + account.getId() + "/transactions")
                                        .with(user(customUserDetails)))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isOk())
                        .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Another"));
        assertTrue(result.getResponse().getContentAsString().contains("Money"));
        assertTrue(result.getResponse().getContentAsString().contains("transfer :D"));
    }

    @Test
        // @WithMockUser(username = "mister", password = "mister", roles = {"OWNER"})
//    @WithUserDetails(value = "mister"/*, userDetailsServiceBeanName = "userDetailsService"*/)
    void getTransactionsByAccount_recipentAccount() throws Exception {
        Account account = accountRepository.findAll().get(3);
        System.out.println("account ID: " + account.getId());

        User user = account.getPrimaryOwner();
        UserDetails customUserDetails =(UserDetails) user;
        System.out.println(customUserDetails);
        System.out.println("CUSTOMER DETAILS USERNAME: " + customUserDetails.getUsername());

        MvcResult result =
                mockMvc.perform(
                                get("/accounts/" + account.getId() + "/transactions")
                                        .with(user(customUserDetails)))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isOk())
                        .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Another"));
        assertTrue(result.getResponse().getContentAsString().contains("Money"));
        assertTrue(result.getResponse().getContentAsString().contains("transfer :D"));
    }
}
