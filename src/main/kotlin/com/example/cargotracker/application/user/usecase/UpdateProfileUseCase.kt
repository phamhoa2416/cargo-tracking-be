package com.example.cargotracker.application.user.usecase

import com.example.cargotracker.application.user.dto.UserDto
import com.example.cargotracker.application.user.mapper.UserMapper
import com.example.cargotracker.domain.user.entity.User
import com.example.cargotracker.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UpdateProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(
        userId: UUID,
        fullName: String?,
        phoneNumber: String?,
        address: String?
    ): Result<UserDto> {
        val existingUser = userRepository.getById(userId)
            ?: return Result.failure(NoSuchElementException("User not found with ID: $userId"))

        if (!existingUser.isActive) {
            return Result.failure(IllegalStateException("User account is inactive"))
        }

        val updatedUser = User(
            id = existingUser.id,
            createdAt = existingUser.createdAt,
            updatedAt = java.time.Instant.now(),
            username = existingUser.username,
            email = existingUser.email,
            passwordHash = existingUser.passwordHash,
            fullName = fullName ?: existingUser.fullName,
            phoneNumber = phoneNumber ?: existingUser.phoneNumber,
            role = existingUser.role,
            address = address ?: existingUser.address,
            isActive = existingUser.isActive
        )

        val success = userRepository.update(updatedUser)
        if (!success) {
            return Result.failure(IllegalStateException("Failed to update user profile"))
        }

        val savedUser = userRepository.getById(userId)
            ?: return Result.failure(IllegalStateException("Failed to retrieve updated user"))

        return Result.success(UserMapper.toDto(savedUser))
    }
}

