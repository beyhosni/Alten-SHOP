package com.alten.shop.dto;

public record AuthResponse(
        String token,
        String email,
        String username) {
}
