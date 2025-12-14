package com.example.cargotracker.infrastructure.security

import com.example.cargotracker.domain.user.types.UserRole
import java.util.UUID

data class JwtClaims(
    val userId: UUID,
    val email: String,
    val role: UserRole
)

