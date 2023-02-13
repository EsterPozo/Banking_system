package com.ironhack.demosecurityjwt.repositories.account;

import com.ironhack.demosecurityjwt.models.account.Checking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingRepository extends JpaRepository <Checking, Long> {
}
