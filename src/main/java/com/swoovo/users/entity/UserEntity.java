package com.swoovo.users.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private long id;

    private String name;

    private String surname;

    private String email;

    private BigInteger phoneNumber;

    private int age;

    private String avatarFilePath;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @ElementCollection
    private List<String> deviceTokens = new ArrayList<>();
}
