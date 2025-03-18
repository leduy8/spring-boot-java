package com.leduy8.springbootjava.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserUpdateRequestDTO(@NotBlank String name) {}
