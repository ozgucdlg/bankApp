package com.restApi.bankApp.dataAccess;

import com.restApi.bankApp.entities.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Integer> {
    Optional<Auth> findByUsername(String username);
    Optional<Auth> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 