package com.leduy8.springbootjava.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserCreateRequestDTO(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password
) {}

