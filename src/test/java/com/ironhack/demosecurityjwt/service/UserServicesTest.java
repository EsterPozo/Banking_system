package com.ironhack.demosecurityjwt.service;

import com.ironhack.demosecurityjwt.dtos.account.AccountDTO;
import com.ironhack.demosecurityjwt.dtos.user.AccountHolderDTO;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import com.ironhack.demosecurityjwt.models.user.Address;
import com.ironhack.demosecurityjwt.models.user.ThirdParty;
import com.ironhack.demosecurityjwt.repositories.account.AccountRepository;
import com.ironhack.demosecurityjwt.repositories.user.AccountHolderRepository;
import com.ironhack.demosecurityjwt.repositories.user.ThirdPartyRepository;
import com.ironhack.demosecurityjwt.services.impl.user.AccountHolderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
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
    private AccountHolderService accountHolderService;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        AccountHolder accountHolder = new AccountHolder(
                "Alejandro Martinez",
                LocalDate.of(1984, 4, 14),
                new Address("Calle Velázquez 1", "Gijón", "33201"));
        accountHolder.setUsername("username1");
        accountHolder.setPassword("password");
        //AccountHolder accountHolder2 = new AccountHolder("Alejandro Martínez", LocalDate.of(1984, 4, 14), new Address("Calle Corrida", "Gijón", "33201"));
        //ThirdParty thirdPartyUser = new ThirdParty("Google", "Hola");

        accountHolderRepository.save(accountHolder);
        //thirdPartyRepository.save(thirdPartyUser);
    }

    @AfterEach
    void tearDown() {
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();

    }

    @Test
    void getOwners() {
        List<AccountHolder> owners = accountHolderService.getOwners();
        assertEquals(1, owners.size());
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
        assertEquals(2, owners.size());
       assertEquals("Ana Fernández", owners.get(owners.size()-1).getName());
    }

}
