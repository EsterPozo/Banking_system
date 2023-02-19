package com.ironhack.demosecurityjwt.service;

import com.ironhack.demosecurityjwt.dtos.account.AccountDTO;
import com.ironhack.demosecurityjwt.dtos.user.AccountHolderDTO;
import com.ironhack.demosecurityjwt.dtos.user.ThirdPartyDTO;
import com.ironhack.demosecurityjwt.dtos.user.UserDTO;
import com.ironhack.demosecurityjwt.models.user.*;
import com.ironhack.demosecurityjwt.repositories.account.AccountRepository;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.repositories.user.AdminRepository;
import com.ironhack.demosecurityjwt.repositories.user.ThirdPartyRepository;
import com.ironhack.demosecurityjwt.repositories.user.UserRepository;
import com.ironhack.demosecurityjwt.services.impl.user.AccountHolderService;
import com.ironhack.demosecurityjwt.services.impl.user.AdminService;
import com.ironhack.demosecurityjwt.services.impl.user.ThirdPartyService;
import com.ironhack.demosecurityjwt.services.impl.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserServicesTest {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private AccountHolderService accountHolderService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminService adminService;



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

//        AccountHolder accountHolder = new AccountHolder(
//                "Alejandro Martinez",
//                LocalDate.of(1984, 4, 14),
//                new Address("Calle Velázquez 1", "Gijón", "33201"));
//        accountHolder.setUsername("username1");
//        accountHolder.setPassword("password");
//
//        AccountHolder accountHolder2 = new AccountHolder("Alba Pou", "alba_pou", "1234", LocalDate.of(1990, 4, 14), new Address("Calle Pizarro", "Mataro", "08032"));
//        //ThirdParty thirdPartyUser = new ThirdParty("Google", "Hola");
//
//        accountHolderRepository.save(accountHolder);
//        userService.addRoleToUser("username1","ROLE_ACCOUNT_HOLDER");
//        accountHolderRepository.save(accountHolder2);
//       // userService.addRoleToUser("alba_pou", "ROLE_ACCOUNT_HOLDER" );
//        //thirdPartyRepository.save(thirdPartyUser);

        Admin admin = new Admin();
        admin.setName("Alejandro");
        admin.setUsername("admin");
        admin.setPassword("ironhack");
        adminRepository.save(admin);
    }

    @AfterEach
    void tearDown() {
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
        userRepository.deleteAll();
        accountRepository.deleteAll();


    }

    @Test
    void getOwners() {
        List<AccountHolder> owners = accountHolderService.getOwners();
        assertEquals(2, owners.size());
    }

    @Test
    void getOwnerById() {

        AccountHolder owner = accountHolderRepository.findByName("Alejandro Martinez");
        Long ownerId = owner.getId();

        Optional<AccountHolder> ownerFound = accountHolderService.getOwnerById(ownerId);
        assertTrue(ownerFound.isPresent());
        assertTrue(ownerFound.get().getName().equalsIgnoreCase("Alejandro Martinez"));
    }

    @Test
    void addAccountHolder() {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        accountHolderDTO.setName("Ana Fernández");
        accountHolderDTO.setDateOfBirth("2000-02-09");
        accountHolderDTO.setStreet("Calle Mayor");
        accountHolderDTO.setCity("Madrid");
        accountHolderDTO.setPostalCode("28080");
        accountHolderDTO.setUsername("username3");
        accountHolderDTO.setPassword("password3");

        AccountHolder accountHolder = accountHolderService.addAccountHolder(accountHolderDTO);

        List<AccountHolder> owners = accountHolderRepository.findAll();
        assertEquals(3, owners.size());
       assertEquals("Ana Fernández", owners.get(owners.size()-1).getName());
    }

    @Test
    void addThirdPartyUser() {

        ThirdPartyDTO thirdPartyUserDTO = new ThirdPartyDTO();
        thirdPartyUserDTO.setName("Hello World");
        thirdPartyUserDTO.setHashedKey("W0RLDH3LL0");
        thirdPartyUserDTO.setUsername("username");
        thirdPartyUserDTO.setPassword("1234");

        ThirdParty thirdPartyUser = thirdPartyService.addThirdParty(thirdPartyUserDTO);

        List<User> owners = userRepository.findAll();
        assertEquals(5, owners.size());
        assertEquals("Hello World", owners.get(owners.size()-1).getName());
    }

    @Test
    void getAdmins() {
        assertEquals(1, adminService.getAdmins().size());
    }

    @Test
    void addAdmin() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Admin 2");
        userDTO.setUsername("admin2");
        userDTO.setPassword("password");
        adminService.addAdmin(userDTO);

        assertEquals(2, adminRepository.findAll().size());
        assertEquals(9, userRepository.findAll().size());
    }


}
