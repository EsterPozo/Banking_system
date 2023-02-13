package com.ironhack.demosecurityjwt.repositories.account;

import com.ironhack.demosecurityjwt.models.account.StudentChecking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCheckingRepository extends JpaRepository <StudentChecking, Long> {
}
