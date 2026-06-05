package com.swoovo.users.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@RequiredArgsConstructor
public final class UserResponse {
    private final Long id;
    private final String name;
    private final String surname;
    private final String email;
    private final BigInteger phoneNumber;
    private final String userRole;

    @Setter
    private String avatarUrl;
}
