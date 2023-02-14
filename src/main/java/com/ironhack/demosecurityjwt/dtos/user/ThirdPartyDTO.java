package com.ironhack.demosecurityjwt.dtos.user;

import jakarta.validation.constraints.NotBlank;

public class ThirdPartyDTO extends UserDTO {
    @NotBlank(message = "Hashed Key is required")
    private String hashedKey;

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }
}
