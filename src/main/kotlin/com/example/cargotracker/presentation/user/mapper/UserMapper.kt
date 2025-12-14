package com.example.cargotracker.presentation.user.mapper

import com.example.cargotracker.application.user.dto.UserDto
import com.example.cargotracker.presentation.user.dto.response.UserResponse
import java.time.Instant

object UserMapper {
    fun toResponse(dto: UserDto): UserResponse {
        return UserResponse(
            id = dto.id,
            username = dto.username,
            email = dto.email,
            fullName = dto.fullName,
            phoneNumber = dto.phoneNumber,
            role = dto.role,
            defaultAddress = dto.address,
            isActive = dto.isActive,
            createdAt = dto.createdAt ?: Instant.now()
        )
    }
}

