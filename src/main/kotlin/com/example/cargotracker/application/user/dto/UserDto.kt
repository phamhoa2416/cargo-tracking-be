package com.example.cargotracker.application.user.dto

import com.example.cargotracker.domain.user.types.UserRole
import java.util.UUID
import java.time.Instant

data class UserDto(
    val id: UUID,
    val username: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String?,
    val role: UserRole,
    val address: String?,
    val isActive: Boolean,
    val createdAt: Instant?,
    val updatedAt: Instant?
)

