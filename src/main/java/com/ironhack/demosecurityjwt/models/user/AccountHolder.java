package com.ironhack.demosecurityjwt.models.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ironhack.demosecurityjwt.models.account.Account;
import com.ironhack.demosecurityjwt.repositories.user.RoleRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Entity
public class AccountHolder extends User {
//    The AccountHolders should be able to access their own accounts and only their accounts when passing the correct credentials using Basic Auth. AccountHolders have:
//
//    A name (FROM USER)
//    Date of birth
//    A primaryAddress (which should be a separate address class)
//    An optional mailingAddress

//    @Autowired
//    private RoleRepository roleRepository;

    private LocalDate dateOfBirth;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "primary_street")),
            @AttributeOverride(name = "city", column = @Column(name = "primary_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "primary_postal_code"))
    })
    @NotNull
    private Address primaryAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "mail_street")),
            @AttributeOverride(name = "city", column = @Column(name = "mail_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "mail_postal_code"))
    })
    private Address mailingAddress;

    // link the primaryaccounts of the ah and the secondaries
    @JsonBackReference
    @OneToMany(mappedBy = "primaryOwner", fetch = FetchType.EAGER/*, orphanRemoval = true*/)
    @Fetch(FetchMode.SUBSELECT)
    private List<Account> primaryAccounts;
    @JsonBackReference
    @OneToMany(mappedBy = "secondaryOwner", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Account> secondaryAccounts;


    public AccountHolder() {
       // Role role = roleRepository.save(new Role("ROLE_ACCOUNT_HOLDER"));
//        Role role = roleRepository.findByName("ROLE_ACCOUNT_HOLDER");
       // this.setRoles();

    }

    public AccountHolder( String name,LocalDate dateOfBirth, Address primaryAddress, Address mailingAddress ) {
        //utilizar super completo
        super(name);
        this.dateOfBirth = dateOfBirth;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = mailingAddress;

        //findbyname role
      //  Role role = roleRepository.findByName("ROLE_ACCOUNT_HOLDER");
//        this.getRoles().add(role);
    }

    public AccountHolder(String name, String username, String password, LocalDate dateOfBirth, Address primaryAddress) {
        super(name, username, password, List.of(new Role(2L,"ROLE_ACCOUNT_HOLDER")));
        this.dateOfBirth = dateOfBirth;
        this.primaryAddress = primaryAddress;
    }

    public AccountHolder(String name, String username, String password, Collection<Role> roles, LocalDate dateOfBirth, Address primaryAddress, Address mailingAddress) {
        super(name, username, password, roles);
        this.dateOfBirth = dateOfBirth;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = mailingAddress;
    }

    public AccountHolder(String name, LocalDate dateOfBirth, Address primaryAddress) {
        super(name);
        this.dateOfBirth = dateOfBirth;
        this.primaryAddress = primaryAddress;
//        Role role = roleRepository.findByName("ROLE_ACCOUNT_HOLDER");
//        this.getRoles().add(role);
    }

    public List<Account> getPrimaryAccounts() {
        return primaryAccounts;
    }

    public void setPrimaryAccounts(List<Account> primaryAccounts) {
        this.primaryAccounts = primaryAccounts;
    }

    public List<Account> getSecondaryAccounts() {
        return secondaryAccounts;
    }

    public void setSecondaryAccounts(List<Account> secondaryAccounts) {
        this.secondaryAccounts = secondaryAccounts;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }


}
