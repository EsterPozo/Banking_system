package com.ironhack.demosecurityjwt.repositories.user;

import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {
}
