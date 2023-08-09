package com.restApi.bankApp.entities;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="account")
public class Account {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="accountHolderName")
    private String accountHolderName;

    @Column(name="balance")
    private double balance;

}
