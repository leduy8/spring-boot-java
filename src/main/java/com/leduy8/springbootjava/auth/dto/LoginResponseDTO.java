package com.leduy8.springbootjava.auth.dto;

import lombok.Builder;

@Builder
public record LoginResponseDTO(
        String accessToken
) {
    public static LoginResponseDTO of(String accessToken) {
        return LoginResponseDTO.builder().accessToken(accessToken).build();
    }
}

