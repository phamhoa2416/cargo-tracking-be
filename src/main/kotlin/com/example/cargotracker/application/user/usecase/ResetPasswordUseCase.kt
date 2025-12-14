package com.example.cargotracker.application.user.usecase

import com.example.cargotracker.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ResetPasswordUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(
        token: String,
        newPasswordHash: String
    ): Result<Unit> {
        val resetToken = userRepository.getPasswordResetToken(token)
            ?: return Result.failure(IllegalArgumentException("Invalid or expired reset token"))

        if (!resetToken.isActive()) {
            return Result.failure(IllegalStateException("Reset token has expired or already been used"))
        }

        val user = userRepository.getById(resetToken.userId)
            ?: return Result.failure(NoSuchElementException("User not found"))

        if (!user.isActive) {
            return Result.failure(IllegalStateException("User account is inactive"))
        }

        val passwordUpdated = userRepository.updatePassword(resetToken.userId, newPasswordHash)
        if (!passwordUpdated) {
            return Result.failure(IllegalStateException("Failed to update password"))
        }

        val tokenMarked = userRepository.markTokenAsUsed(resetToken.id)
        if (!tokenMarked) {
            return Result.failure(IllegalStateException("Failed to mark token as used"))
        }

        return Result.success(Unit)
    }
}

