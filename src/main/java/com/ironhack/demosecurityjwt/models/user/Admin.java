package com.ironhack.demosecurityjwt.models.user;

import jakarta.persistence.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
public class Admin extends User{
    //Admins should be able to access the balance for any account and to modify it.


    public Admin() {
        super(List.of(new Role(1L,"ROLE_ADMIN")));

    }

    public Admin(String name, String username, String password) {
        super(name, username, password, List.of(new Role(1L,"ROLE_ADMIN")));
    }
}
