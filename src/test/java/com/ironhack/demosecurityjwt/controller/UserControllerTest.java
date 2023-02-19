package com.ironhack.demosecurityjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.demosecurityjwt.dtos.user.AccountHolderDTO;
import com.ironhack.demosecurityjwt.dtos.user.ThirdPartyDTO;
import com.ironhack.demosecurityjwt.dtos.user.UserDTO;
import com.ironhack.demosecurityjwt.models.Money;
import com.ironhack.demosecurityjwt.models.account.*;
import com.ironhack.demosecurityjwt.models.user.*;
import com.ironhack.demosecurityjwt.repositories.account.*;
import com.ironhack.demosecurityjwt.repositories.transaction.TransactionRepository;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.repositories.user.AdminRepository;
import com.ironhack.demosecurityjwt.repositories.user.ThirdPartyRepository;
import com.ironhack.demosecurityjwt.services.impl.user.AccountHolderService;
import com.ironhack.demosecurityjwt.services.impl.user.ThirdPartyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerTest {

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
    private AccountHolderService accountHolderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ThirdPartyService thirdPartyService;

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

    //@WithMockUser(username = "admin", password = "ironhack", roles = {"ADMIN"})
    @Test
    void getOwners() throws Exception {

        MvcResult result = mockMvc.perform(
                        get("/bank/users/owners"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Mr. Account Holder"));
        assertTrue(result.getResponse().getContentAsString().contains("Mrs. Account Holder"));
    }

   // @WithMockUser(username = "admin", password = "ironhack", roles = {"ADMIN"})
    @Test
    void getOwnerById() throws Exception {

        AccountHolder owner = accountHolderRepository.findByName("Mr. Account Holder");

        MvcResult result =
                mockMvc.perform(
                                get("/bank/users/owners/" + owner.getId()))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isOk())
                        .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Mr. Account Holder"));
    }

    @WithMockUser(username = "admin", password = "ironhack", roles = {"ADMIN"})
    @Test
    void addAccountHolder() throws Exception {

//        AccountHolder ah = new AccountHolder(
//                "Mr. Account Holder", "mister", "password1",
//                LocalDate.of(1990, 4, 14),
//                new Address("Calle Velázquez 1", "Gijón", "33201"));

       AccountHolderDTO ah = new AccountHolderDTO();
        ah.setName("Mrs. Account Holder");
        ah.setDateOfBirth("1985-04-14");
        ah.setStreet("Street");
        ah.setCity("City");
        ah.setPostalCode("PostalCode");
        ah.setUsername("username");
        ah.setPassword("password");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String body = objectMapper.writeValueAsString(ah);

        MvcResult result =
                mockMvc.perform(
                                post("/bank/users/owners/ah")
                                        .content(body)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isCreated())
                        .andReturn();

        List<AccountHolder> owners = accountHolderService.getOwners();
        assertEquals(3, owners.size());
        assertTrue(result.getResponse().getContentAsString().contains("Mrs. Account Holder"));
    }

    //@WithMockUser(username = "admin", password = "ironhack", roles = {"ADMIN"})
    @Test
    void addThirdPartyUser() throws Exception {

        ThirdPartyDTO tpu = new ThirdPartyDTO();
        tpu.setName("Another TPU");
        tpu.setHashedKey("anotherHashedKey");
        tpu.setUsername("username");
        tpu.setPassword("password");
        String body = objectMapper.writeValueAsString(tpu);

        MvcResult result =
                mockMvc.perform(
                                post("/bank/users/owners/tpu")
                                        .content(body)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isCreated())
                        .andReturn();

        List<ThirdParty> tp = thirdPartyRepository.findAll();
        assertEquals(2,tp.size());
        assertEquals("Another TPU", tp.get(tp.size()-1).getName());
    }

    @Test
    //@WithMockUser(username = "admin", password = "ironhack", roles = {"ADMIN"})
    void getAdmins() throws Exception {

        Admin admin2 = new Admin();
        admin2.setUsername("admin2");
        admin2.setPassword("ironhack2");
        adminRepository.save(admin2);

        MvcResult result =
                mockMvc.perform(
                                get("/bank/users/admins"))
                        .andExpect(status().isOk())
                        .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("admin"));
        assertTrue(result.getResponse().getContentAsString().contains("admin2"));

    }

    @Test
    //@WithMockUser(username = "admin", password = "ironhack", roles = {"ADMIN"})
    void addAdmin() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Another Admin");
        userDTO.setUsername("admin_");
        userDTO.setPassword("helloworld");
        String body = objectMapper.writeValueAsString(userDTO);

        MvcResult result =
                mockMvc.perform(
                                post("/bank/users/admins")
                                        .content(body)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isCreated())
                        .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("admin_"));
        assertTrue(result.getResponse().getContentAsString().contains("Another Admin"));
        assertEquals(2, adminRepository.findAll().size());
    }


}
