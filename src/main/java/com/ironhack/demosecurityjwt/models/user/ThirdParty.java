package com.ironhack.demosecurityjwt.models.user;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class ThirdParty extends User {

    //The ThirdParty Accounts have a hashed key and a name.
    private String hashedKey;

    public ThirdParty(String hashedKey) {
        this.hashedKey = hashedKey;
        this.getRoles().add(new Role("ROLE_THIRD_PARTY"));
    }

    public ThirdParty(String name, String hashedKey) {
        super(name);
        this.hashedKey = hashedKey;
        this.getRoles().add(new Role("ROLE_THIRD_PARTY"));

    }

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }
}
