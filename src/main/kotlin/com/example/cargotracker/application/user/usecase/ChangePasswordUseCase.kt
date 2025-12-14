package com.example.cargotracker.application.user.usecase

import com.example.cargotracker.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChangePasswordUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(
        userId: UUID,
        newPasswordHash: String
    ): Result<Unit> {
        val user = userRepository.getById(userId)
            ?: return Result.failure(NoSuchElementException("User not found with ID: $userId"))

        if (!user.isActive) {
            return Result.failure(IllegalStateException("User account is inactive"))
        }

        val success = userRepository.updatePassword(userId, newPasswordHash)
        if (!success) {
            return Result.failure(IllegalStateException("Failed to update password"))
        }

        return Result.success(Unit)
    }
}

