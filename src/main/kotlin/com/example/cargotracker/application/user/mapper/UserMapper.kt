package com.example.cargotracker.application.user.mapper

import com.example.cargotracker.application.user.dto.UserDto
import com.example.cargotracker.domain.user.entity.User

object UserMapper {
    fun toDto(user: User): UserDto {
        return UserDto(
            id = user.id,
            username = user.username,
            email = user.email,
            fullName = user.fullName,
            phoneNumber = user.phoneNumber,
            role = user.role,
            address = user.address,
            isActive = user.isActive,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}