package com.ironhack.demosecurityjwt.dtos;

import jakarta.validation.constraints.NotBlank;

public class UserDTO {
    /**
     * The name of the user
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * The username used to log in
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The password used to log in
     */
    @NotBlank(message = "Password is required")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
