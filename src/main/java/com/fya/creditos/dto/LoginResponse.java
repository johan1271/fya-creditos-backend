package com.fya.creditos.dto;

public record LoginResponse(
        String token,
        String username,
        String fullName
) {
}
