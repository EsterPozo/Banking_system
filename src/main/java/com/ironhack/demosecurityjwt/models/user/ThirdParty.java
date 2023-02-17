package com.ironhack.demosecurityjwt.models.user;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
public class ThirdParty extends User {

    //The ThirdParty Accounts have a hashed key and a name.
    private String hashedKey;

    public ThirdParty(String hashedKey) {
        super(List.of(new Role(3L,"ROLE_THIRD_PARTY")));
        this.hashedKey = hashedKey;

    }

    public ThirdParty(String name, String hashedKey) {
        super(name,List.of(new Role(3L,"ROLE_THIRD_PARTY")));
        this.hashedKey = hashedKey;


    }

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }
}
