package com.ironhack.demosecurityjwt.services.impl.user;

import com.ironhack.demosecurityjwt.dtos.user.UserDTO;
import com.ironhack.demosecurityjwt.models.user.Admin;
import com.ironhack.demosecurityjwt.models.user.Role;
import com.ironhack.demosecurityjwt.repositories.user.AdminRepository;
import com.ironhack.demosecurityjwt.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;


    public List<Admin> getAdmins() {
        return adminRepository.findAll();
    }


    public Admin addAdmin(UserDTO userDTO) {
        Admin admin = new Admin();
        admin.setUsername(userDTO.getUsername());
        admin.setPassword(userDTO.getPassword());
        admin.setName(userDTO.getName());
       // admin.getRoles().add(new Role("ROLE_ADMIN"));
        return adminRepository.save(admin);
    }
}
