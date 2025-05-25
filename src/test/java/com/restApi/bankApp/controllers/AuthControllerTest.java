package com.restApi.bankApp.controllers;

import com.restApi.bankApp.business.abstracts.AuthService;
import com.restApi.bankApp.business.abstracts.NotificationService;
import com.restApi.bankApp.entities.Auth;
import com.restApi.bankApp.entities.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<Auth> expectedUsers = Arrays.asList(
            new Auth("user1", "pass1", "user1@test.com"),
            new Auth("user2", "pass2", "user2@test.com")
        );
        when(authService.getAllUsers()).thenReturn(expectedUsers);

        // Act
        ResponseEntity<?> response = authController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedUsers, response.getBody());
        verify(authService, times(1)).getAllUsers();
    }

    @Test
    void register_ShouldReturnRegisteredUser() {
        // Arrange
        Auth auth = new Auth("newuser", "password", "newuser@test.com");
        Account account = new Account();
        account.setId(1);
        auth.setAccount(account);
        
        when(authService.register(any(Auth.class))).thenReturn(auth);
        doNothing().when(notificationService).sendNotification(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<?> response = authController.register(auth);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(auth, response.getBody());
        verify(authService, times(1)).register(auth);
        verify(notificationService, times(2)).sendNotification(anyString(), anyString(), anyString());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenRegistrationFails() {
        // Arrange
        Auth auth = new Auth("newuser", "password", "newuser@test.com");
        when(authService.register(any(Auth.class))).thenThrow(new RuntimeException("Registration failed"));

        // Act
        ResponseEntity<?> response = authController.register(auth);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Registration failed", response.getBody());
        verify(authService, times(1)).register(auth);
        verify(notificationService, never()).sendNotification(anyString(), anyString(), anyString());
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        // Arrange
        Auth auth = new Auth("user", "password", "user@test.com");
        when(authService.login("user", "password")).thenReturn(Optional.of(auth));

        // Act
        ResponseEntity<?> response = authController.login(auth);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(auth, response.getBody());
        verify(authService, times(1)).login("user", "password");
    }

    @Test
    void login_ShouldReturnBadRequest_WhenCredentialsAreInvalid() {
        // Arrange
        Auth auth = new Auth("user", "wrongpassword", "user@test.com");
        when(authService.login("user", "wrongpassword")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = authController.login(auth);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid credentials", response.getBody());
        verify(authService, times(1)).login("user", "wrongpassword");
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        // Arrange
        String username = "testuser";
        Auth expectedUser = new Auth(username, "password", "test@test.com");
        when(authService.getAuthByUsername(username)).thenReturn(Optional.of(expectedUser));

        // Act
        ResponseEntity<?> response = authController.getUserByUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedUser, response.getBody());
        verify(authService, times(1)).getAuthByUsername(username);
    }

    @Test
    void getUserByUsername_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistentuser";
        when(authService.getAuthByUsername(username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = authController.getUserByUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        verify(authService, times(1)).getAuthByUsername(username);
    }

    @Test
    void updateAuth_ShouldReturnUpdatedUser() {
        // Arrange
        int userId = 1;
        Auth auth = new Auth("updateduser", "newpassword", "updated@test.com");
        auth.setId(userId);
        when(authService.updateAuth(any(Auth.class))).thenReturn(auth);

        // Act
        ResponseEntity<?> response = authController.updateAuth(userId, auth);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(auth, response.getBody());
        verify(authService, times(1)).updateAuth(auth);
    }

    @Test
    void updateAuth_ShouldReturnBadRequest_WhenUpdateFails() {
        // Arrange
        int userId = 1;
        Auth auth = new Auth("updateduser", "newpassword", "updated@test.com");
        auth.setId(userId);
        when(authService.updateAuth(any(Auth.class))).thenThrow(new RuntimeException("Update failed"));

        // Act
        ResponseEntity<?> response = authController.updateAuth(userId, auth);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Update failed", response.getBody());
        verify(authService, times(1)).updateAuth(auth);
    }

    @Test
    void deleteAuth_ShouldReturnOk_WhenDeletionSuccessful() {
        // Arrange
        int userId = 1;
        doNothing().when(authService).deleteAuth(userId);

        // Act
        ResponseEntity<?> response = authController.deleteAuth(userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(authService, times(1)).deleteAuth(userId);
    }

    @Test
    void deleteAuth_ShouldReturnBadRequest_WhenDeletionFails() {
        // Arrange
        int userId = 1;
        doThrow(new RuntimeException("Deletion failed")).when(authService).deleteAuth(userId);

        // Act
        ResponseEntity<?> response = authController.deleteAuth(userId);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Deletion failed", response.getBody());
        verify(authService, times(1)).deleteAuth(userId);
    }

    @Test
    void logout_ShouldReturnSuccess() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act
        ResponseEntity<?> response = authController.logout(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Logged out successfully", response.getBody());
    }

    @Test
    void logout_ShouldReturnBadRequest_WhenErrorOccurs() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("error", new RuntimeException("Logout error"));

        // Act
        ResponseEntity<?> response = authController.logout(request);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("Error during logout"));
    }
} 