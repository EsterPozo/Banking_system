package com.ironhack.demosecurityjwt.repositories.account;

import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.models.user.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository <Account, Long> {

    List<Account> findByPrimaryOwner(AccountHolder owner);
    List<Account> findByPrimaryOwnerName(String name);

    List<Account> findBySecondaryOwner(AccountHolder owner);

    List<Account> findByPrimaryOwnerOrSecondaryOwner(AccountHolder primaryOwner, AccountHolder secondaryOwner);


}
