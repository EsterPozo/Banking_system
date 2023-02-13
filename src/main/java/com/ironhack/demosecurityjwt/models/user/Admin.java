package com.ironhack.demosecurityjwt.models.user;

import jakarta.persistence.Entity;

import java.util.Collection;
import java.util.Collections;

@Entity
public class Admin extends User{
    //Admins should be able to access the balance for any account and to modify it.


    public Admin() {
        this.getRoles().add(new Role("ROLE_ADMIN"));
    }

}
