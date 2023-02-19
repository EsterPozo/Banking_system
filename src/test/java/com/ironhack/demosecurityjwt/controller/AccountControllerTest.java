package com.ironhack.demosecurityjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.demosecurityjwt.dtos.account.AccountDTO;
import com.ironhack.demosecurityjwt.dtos.transaction.TransactionDTO;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.user.*;
import com.ironhack.demosecurityjwt.repositories.account.*;
import com.ironhack.demosecurityjwt.repositories.transaction.TransactionRepository;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.repositories.user.AdminRepository;
import com.ironhack.demosecurityjwt.repositories.user.ThirdPartyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

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
    }

    @Test
   // @WithMockUser(username = "admin", password = "ironhack", roles = {"ADMIN"})
    void addChecking() throws Exception {
        List<AccountHolder> owners = accountHolderRepository.findAll();
        Long idOwner = owners.get(1).getId();

        AccountDTO check = new AccountDTO();
        check.setBalance(BigDecimal.valueOf(1000L));
        check.setSecretKey("12345678");
        check.setOwnerId(idOwner);
        String body = objectMapper.writeValueAsString(check);

        MvcResult result =
                mockMvc.perform(
                                post("/bank/accounts/checking/")
                                        .content(body)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(owners.get(1).getName()));
        assertTrue(result.getResponse().getContentAsString().contains(check.getSecretKey()));
        // student checking account because age < 24
        assertTrue(result.getResponse().getContentAsString().contains(studentCheckingRepository.findAll().get(0).getPrimaryOwner().getName()));
    }

    @Test
    @WithMockUser(username = "admin", password = "ironhack", roles = {"ADMIN"})
    void addSavings() throws Exception {
        List<AccountHolder> owners = accountHolderRepository.findAll();
        Long idOwner = owners.get(1).getId();

        AccountDTO sav = new AccountDTO();
        sav.setBalance(BigDecimal.valueOf(2000L));
        sav.setSecretKey("12345678");
        sav.setInterestRate(new BigDecimal("0.1234"));
        sav.setOwnerId(idOwner);
        String body = objectMapper.writeValueAsString(sav);

        MvcResult result =
                mockMvc.perform(
                                post("/bank/accounts/savings/")
                                        .content(body)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(owners.get(1).getName()));
       assertTrue(result.getResponse().getContentAsString().contains(sav.getSecretKey()));
       assertTrue(result.getResponse().getContentAsString().contains("0.1234")); // interestRate
        assertTrue(result.getResponse().getContentAsString().contains("1000.00")); // default minimumBalance
    }

    @Test
    //@WithMockUser(username = "admin", password = "ironhack")
    void addCreditCard() throws Exception {
        List<AccountHolder> owners = accountHolderRepository.findAll();
        System.out.println(owners.size());
        Long idOwner = owners.get(1).getId();

        AccountDTO cc = new AccountDTO();
        cc.setBalance(BigDecimal.valueOf(2000L));
        cc.setInterestRate(new BigDecimal("0.1234"));
        cc.setCreditLimit(BigDecimal.valueOf(2121));
        cc.setOwnerId( idOwner);
        String body = objectMapper.writeValueAsString(cc);

                MvcResult result =
                mockMvc.perform(
                                post("/bank/accounts/creditcard/")
                                        .content(body)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

       assertTrue(result.getResponse().getContentAsString().contains(owners.get(1).getName()));
       assertTrue(result.getResponse().getContentAsString().contains("0.1234")); // interestRate
       assertTrue(result.getResponse().getContentAsString().contains("2121.00")); // default creditLimit
    }

    @Test
    void transferMoney() throws Exception {
        List<AccountHolder> owners = accountHolderRepository.findAll();
        Long idOwner = owners.get(0).getId();
        Account fromAccount = owners.get(0).getPrimaryAccounts().get(1);
        Account toAccount = owners.get(0).getPrimaryAccounts().get(0);

        TransactionDTO transfer = new TransactionDTO();
        transfer.setAmount(new BigDecimal("200.00"));
        transfer.setToAccountId(toAccount.getId());
        transfer.setName(owners.get(0).getName());
        transfer.setDescription("More savings!");
        transfer.setFromAccountId(fromAccount.getId());
        String body = objectMapper.writeValueAsString(transfer);

        AccountHolder user1 = (AccountHolder) fromAccount.getPrimaryOwner();

        UserDetails userDetails = (UserDetails) user1;



        MvcResult result =
                mockMvc.perform(
                                post("/accounts/transfer")
                                        .with(user(userDetails))
                                        .content(body)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(owners.get(0).getName()));
        assertTrue(result.getResponse().getContentAsString().contains("800.00")); // savings account
    }


}
