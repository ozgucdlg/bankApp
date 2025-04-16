package com.restApi.bankApp.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="auth")
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="username", unique = true, nullable = false)
    private String username;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="email", unique = true)
    private String email;

    @Column(name="role")
    private String role;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;
} 