package com.ironhack.demosecurityjwt.controllers.impl;

import com.ironhack.demosecurityjwt.dtos.user.UserDTO;
import com.ironhack.demosecurityjwt.models.user.Admin;
import com.ironhack.demosecurityjwt.services.impl.user.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/bank/users/admins")
    @ResponseStatus(HttpStatus.OK)
    public List<Admin> getAdmins() {

        return adminService.getAdmins();
    }

    @PostMapping("/bank/users/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public Admin addAdmin(@RequestBody UserDTO userDto) {

        return adminService.addAdmin(userDto);
    }
}
