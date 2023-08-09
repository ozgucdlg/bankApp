package com.restApi.bankApp.controller;

import com.restApi.bankApp.business.abstracts.AccountService;
import com.restApi.bankApp.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public Account createAccount(@RequestBody Account account){
        return accountService.createAccount(account);
    }

    @GetMapping("{id}")
    public Account getAccount(@PathVariable int id){
        return accountService.getAccount(id).orElseThrow(()-> new RuntimeException("Account is not found "));
    }

    @PostMapping("/{id}/deposit")
    public Account deposit(@PathVariable int id, @RequestBody Map<String, Double> request){
        Double amount = request.get("amount");
        return accountService.deposit(id, amount);
    }

    @PostMapping("/{id}/withdraw")
    public Account withdraw(@PathVariable int id, @RequestBody Map<String, Double> request) {
        Double amount = request.get("amount");
        return accountService.withdraw(id, amount);
    }
}
