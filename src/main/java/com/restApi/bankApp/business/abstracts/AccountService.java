package com.restApi.bankApp.business.abstracts;

import com.restApi.bankApp.entities.Account;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface AccountService {

    Account createAccount(Account account);
    Optional<Account> getAccount(int id);
    Account deposit(int id, double amount);
    Account withdraw(int id, double amount);

}
