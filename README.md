# REST API with Spring Boot
### Banking System

This project features a banking system backend implementation.
It's based in Java, using the Spring Boot with Spring JPA + Hibernate using MySQL database, and Spring Security.

The API developed here let end users to register new customers, open accounts and associate with them, so it's
possible to make money transfers between these accounts, once proved their ownership using basic authentication and authorization.

Some additional features are:

- Different types of accounts and users
- Automated interests and fees application
- Secured and admin based management

### Included documentation
- Class Diagram_Banking system.pdf : initial class Diagram
- Banking_system_postman_collection.json : extract of Postman trial/usage of methods.

### API Specification

#### Admin only
```
/bank/users
```
- `GET` Read list of all users
``` 
/bank/users/admins
```
- `GET` Read list of all admins

- `POST` Create new admin user
```
/bank/users/owners
```
- `GET` Read list of all owners
 ```
/bank/users/owners/ah
``` 
- `POST` Create new account holder
```
/bank/users/owners/tpu
``` 
- `POST` Create new third party user
 ```
/bank/users/owners/{id}
``` 
- `GET` Read owner
 ```
/bank/users/owners/{id}/accounts
``` 
- `GET` Read accounts of an owner
 ```
/bank/accounts
``` 
- `GET` Read list of all accounts
 ```
/bank/accounts/{id}
 ``` 
- `PATCH` Update balance of the account
 ```
/bank/accounts/checking/
 ``` 
- `POST` Create new checking account with owner or two owners
 ```
/bank/accounts/savings/
 ``` 
- `POST` Create new savings account with owner or two owners
 ```
 /bank/accounts/creditcard/
 ```
- `POST` Create new credit card account with owner or two owners
 ```
 /bank/transactions
 ```
- `GET`  Read list of all transactions

#### Auth owner
 ``` 
 /accounts/{id}
 ``` 
- `GET`  Read account
``` 
/accounts/transfer
 ``` 
- `POST` Create money transfer from account
 ```
 /accounts/{id}/transactions
 ```
- `GET`  Read list of all transactions of account