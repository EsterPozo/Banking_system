package com.ironhack.demosecurityjwt.services.impl.user;

import com.ironhack.demosecurityjwt.dtos.user.ThirdPartyDTO;
import com.ironhack.demosecurityjwt.models.user.Role;
import com.ironhack.demosecurityjwt.models.user.ThirdParty;
import com.ironhack.demosecurityjwt.repositories.user.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyService {
    
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public ThirdParty addThirdParty(ThirdPartyDTO thirdPartyDTO) {

        ThirdParty thirdParty = new ThirdParty();

        thirdParty.setName(thirdPartyDTO.getName());
        thirdParty.setHashedKey(thirdPartyDTO.getHashedKey());
        thirdParty.setUsername(thirdPartyDTO.getUsername());
        thirdParty.setPassword(passwordEncoder.encode(thirdPartyDTO.getPassword()));

        return thirdPartyRepository.save(thirdParty);

    }
}
