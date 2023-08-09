package com.restApi.bankApp.business.concretes;

import com.restApi.bankApp.business.abstracts.AccountService;
import com.restApi.bankApp.dataAccess.AccountRepository;
import com.restApi.bankApp.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class AccountManager implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Override
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }
    @Override
    public Optional<Account> getAccount(int id) {

        return accountRepository.findById(id);
    }
    @Override
    public Account deposit(int id, double amount) {
        Account account= getAccount(id).orElseThrow(()-> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance()+amount);
        return accountRepository.save(account);
    }

    @Override
    public Account withdraw(int id, double amount) {
        Account account= getAccount(id).orElseThrow(()-> new RuntimeException("Äccount not found"));
        if(account.getBalance() < amount){
            throw new RuntimeException("Your funds are not enough");

        }
        account.setBalance(account.getBalance() - amount);
        return accountRepository.save(account);
    }
}
