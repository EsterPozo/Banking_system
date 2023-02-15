package com.ironhack.demosecurityjwt.services.interfaces;

import com.ironhack.demosecurityjwt.dtos.transaction.TransactionDTO;
import com.ironhack.demosecurityjwt.models.account.Account;

public interface IMoneyTransferService {

    Account doMoneyTransfer(TransactionDTO transactionDTO);
}
