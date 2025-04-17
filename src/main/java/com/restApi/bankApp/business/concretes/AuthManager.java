package com.restApi.bankApp.business.concretes;

import com.restApi.bankApp.business.abstracts.AuthService;
import com.restApi.bankApp.business.abstracts.NotificationService;
import com.restApi.bankApp.business.abstracts.AccountService;
import com.restApi.bankApp.dataAccess.AuthRepository;
import com.restApi.bankApp.entities.Auth;
import com.restApi.bankApp.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthManager implements AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private NotificationService notificationService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public Auth register(Auth auth) {
        if (!isUsernameAvailable(auth.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (!isEmailAvailable(auth.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create account if not provided
        if (auth.getAccount() == null) {
            Account newAccount = new Account();
            newAccount.setAccountHolderName(auth.getUsername());
            newAccount.setBalance(0.0);
            auth.setAccount(accountService.createAccount(newAccount, this.getClass().getName()));
        }

        // Encode password before saving
        auth.setPassword(passwordEncoder.encode(auth.getPassword()));
        
        // Set default role if not specified
        if (auth.getRole() == null || auth.getRole().isEmpty()) {
            auth.setRole("USER");
        }

        // Save the auth entity
        Auth savedAuth = authRepository.save(auth);

        // Send welcome notification
        String welcomeMessage = String.format(
            "Welcome %s! Your bank account has been created successfully. Account ID: %d",
            auth.getUsername(),
            auth.getAccount().getId()
        );
        
        notificationService.sendNotification(
            auth.getEmail(),
            "Welcome to Our Bank",
            welcomeMessage
        );

        return savedAuth;
    }

    @Override
    public Optional<Auth> login(String username, String password) {
        Optional<Auth> auth = authRepository.findByUsername(username);
        
        if (auth.isPresent() && passwordEncoder.matches(password, auth.get().getPassword())) {
            return auth;
        }
        
        return Optional.empty();
    }

    @Override
    @Transactional
    public Auth updateAuth(Auth auth) {
        Auth existingAuth = getAuthById(auth.getId())
                .orElseThrow(() -> new RuntimeException("Auth not found"));

        // If updating username, check availability
        if (!auth.getUsername().equals(existingAuth.getUsername()) 
            && !isUsernameAvailable(auth.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        // If updating email, check availability
        if (!auth.getEmail().equals(existingAuth.getEmail()) 
            && !isEmailAvailable(auth.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // If updating password, encode it
        if (!auth.getPassword().equals(existingAuth.getPassword())) {
            auth.setPassword(passwordEncoder.encode(auth.getPassword()));
        }

        return authRepository.save(auth);
    }

    @Override
    @Transactional
    public void deleteAuth(int id) {
        if (!authRepository.existsById(id)) {
            throw new RuntimeException("Auth not found");
        }
        authRepository.deleteById(id);
    }

    @Override
    public Optional<Auth> getAuthById(int id) {
        return authRepository.findById(id);
    }

    @Override
    public Optional<Auth> getAuthByUsername(String username) {
        return authRepository.findByUsername(username);
    }

    @Override
    public Optional<Auth> getAuthByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !authRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !authRepository.existsByEmail(email);
    }

    @Override
    public List<Auth> getAllUsers() {
        return authRepository.findAll();
    }
} 