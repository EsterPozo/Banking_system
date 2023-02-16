package com.ironhack.demosecurityjwt.repositories.account;

import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.account.Checking;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckingRepository extends JpaRepository <Checking, Long> {
    List<Account> findByPrimaryOwner(AccountHolder owner);
    Account findByPrimaryOwnerName(String ownerName);
}
