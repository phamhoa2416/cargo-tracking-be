package com.example.cargotracker.presentation.user.dto.response

data class AuthResponse(
    val user: UserResponse?,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long
)

