package com.leduy8.springbootjava.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDTO(@Email @NotBlank String email, @NotBlank String password) {}
