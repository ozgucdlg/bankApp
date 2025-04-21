package com.restApi.bankApp.dataAccess;

import com.restApi.bankApp.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    /**
     * Finds all accounts with a balance less than the specified amount
     * @param balance The threshold balance
     * @return List of accounts with balance less than the threshold
     */
    List<Account> findByBalanceLessThan(double balance);
}
