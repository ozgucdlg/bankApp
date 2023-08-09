package com.restApi.bankApp.dataAccess;

import com.restApi.bankApp.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer > {
}
