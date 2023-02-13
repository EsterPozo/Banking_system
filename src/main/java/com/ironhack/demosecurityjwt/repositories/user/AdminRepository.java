package com.ironhack.demosecurityjwt.repositories.user;

import com.ironhack.demosecurityjwt.models.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
