package com.ironhack.demosecurityjwt.services.impl;

import com.ironhack.demosecurityjwt.dtos.ThirdPartyDTO;
import com.ironhack.demosecurityjwt.models.user.Role;
import com.ironhack.demosecurityjwt.models.user.ThirdParty;
import com.ironhack.demosecurityjwt.repositories.user.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyService {
    
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;


    public ThirdParty addThirdParty(ThirdPartyDTO thirdPartyDTO) {

        ThirdParty thirdParty = new ThirdParty();

        thirdParty.setName(thirdPartyDTO.getName());
        thirdParty.setHashedKey(thirdPartyDTO.getHashedKey());
        thirdParty.setUsername(thirdParty.getUsername());
        thirdParty.setPassword(thirdParty.getPassword());
        thirdParty.getRoles().add(new Role("ROLE_THIRD_party"));

        return thirdPartyRepository.save(thirdParty);

    }
}
