package com.example.cargotracker.application.user.usecase

import com.example.cargotracker.domain.user.entity.PasswordResetToken
import com.example.cargotracker.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ForgotPasswordUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(
        email: String,
        token: String,
        expiresInMinutes: Long = 60
    ): Result<PasswordResetToken> {
        val user = userRepository.getByEmail(email)
            ?: return Result.failure(NoSuchElementException("User not found with email: $email"))

        if (!user.isActive) {
            return Result.failure(IllegalStateException("User account is inactive"))
        }

        val resetToken = PasswordResetToken(
            userId = user.id,
            token = token,
            expiresAt = Instant.now().plusSeconds(expiresInMinutes * 60),
            used = false
        )

        return try {
            val savedToken = userRepository.createPasswordResetToken(resetToken)
            Result.success(savedToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

