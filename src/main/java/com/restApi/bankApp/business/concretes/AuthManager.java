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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            newAccount.setBalance(5000.0);
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

        // Current date and time for the notification
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        // Send detailed welcome notification to the user
        String userWelcomeMessage = String.format(
            "Welcome %s!\n\n" +
            "Your bank account has been successfully created on %s.\n\n" +
            "Account Details:\n" +
            "- Account ID: %d\n" +
            "- Account Holder: %s\n" +
            "- Initial Balance: $%.2f\n\n" +
            "You can now log in to access your account, make transactions, and manage your finances.\n\n" +
            "If you have any questions or need assistance, please contact our customer support team.\n\n" +
            "Thank you for choosing our bank!",
            auth.getUsername(),
            formattedDateTime,
            auth.getAccount().getId(),
            auth.getAccount().getAccountHolderName(),
            auth.getAccount().getBalance()
        );
        
        notificationService.sendNotification(
            auth.getUsername(),
            "Welcome to Secure Bank - Your Account is Ready!",
            userWelcomeMessage
        );
        
        // Security tips notification
        String securityTips = String.format(
            "Security Tips for Your New Account\n\n" +
            "Dear %s,\n\n" +
            "Congratulations on your new account with a $5,000.00 initial balance!\n\n" +
            "Here are some security tips to help protect your account:\n\n" +
            "1. Never share your password with anyone\n" +
            "2. Use a strong, unique password\n" +
            "3. Enable two-factor authentication when available\n" +
            "4. Be cautious of phishing attempts\n" +
            "5. Regularly check your account for unauthorized transactions\n\n" +
            "Stay safe online!\n" +
            "The Secure Bank Security Team",
            auth.getUsername()
        );
        
        notificationService.sendNotification(
            auth.getUsername(),
            "Important Security Information",
            securityTips
        );
        
        // Also send a notification to system admin about the new user
        String adminNotificationMessage = String.format(
            "New user registration alert!\n\n" +
            "User Details:\n" +
            "- Username: %s\n" +
            "- Email: %s\n" +
            "- Account ID: %d\n" +
            "- Registration Time: %s\n\n" +
            "A new account has been created in the system.",
            auth.getUsername(),
            auth.getEmail(),
            auth.getAccount().getId(),
            formattedDateTime
        );
        
        notificationService.sendNotification(
            "admin@securebank.com", // Admin email
            "New User Registration Alert",
            adminNotificationMessage
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