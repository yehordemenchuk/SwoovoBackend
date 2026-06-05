package com.swoovo.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

public record UserRequest(@NotEmpty String name,
                          @NotEmpty String surname,
                          @NotEmpty @Email String email,
                          @Positive BigInteger phoneNumber,
                          @NotEmpty String userRole,
                          @NotNull MultipartFile avatar) {
}
