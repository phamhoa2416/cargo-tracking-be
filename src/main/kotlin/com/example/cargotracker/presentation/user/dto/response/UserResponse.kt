package com.example.cargotracker.presentation.user.dto.response

import com.example.cargotracker.domain.user.types.UserRole
import java.util.UUID
import java.time.Instant

data class UserResponse(
    val id: UUID,
    val username: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String?,
    val role: UserRole,
    val defaultAddress: String?,
    val isActive: Boolean,
    val createdAt: Instant
)

