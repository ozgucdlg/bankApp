package com.restApi.bankApp.business.abstracts;

import com.restApi.bankApp.entities.Auth;
import java.util.Optional;

public interface AuthService {
    Auth register(Auth auth);
    Optional<Auth> login(String username, String password);
    Auth updateAuth(Auth auth);
    void deleteAuth(int id);
    Optional<Auth> getAuthById(int id);
    Optional<Auth> getAuthByUsername(String username);
    Optional<Auth> getAuthByEmail(String email);
    boolean isUsernameAvailable(String username);
    boolean isEmailAvailable(String email);
} 