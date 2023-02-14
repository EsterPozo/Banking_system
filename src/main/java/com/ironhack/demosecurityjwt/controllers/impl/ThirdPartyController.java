package com.ironhack.demosecurityjwt.controllers.impl;

import com.ironhack.demosecurityjwt.dtos.user.ThirdPartyDTO;
import com.ironhack.demosecurityjwt.models.user.ThirdParty;
import com.ironhack.demosecurityjwt.services.impl.user.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThirdPartyController {

    @Autowired
    private ThirdPartyService thirdPartyService;

    @PostMapping("/bank/users/owners/tpu")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty addThirdPartyUser(@RequestBody ThirdPartyDTO thirdPartyDTO) {
        return thirdPartyService.addThirdParty(thirdPartyDTO);
    }
    }
