package com.example.cargotracker.application.user.usecase

import com.example.cargotracker.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(userId: UUID): Result<Unit> {
        userRepository.getById(userId)
            ?: return Result.failure(NoSuchElementException("User not found with ID: $userId"))

        val success = userRepository.delete(userId)
        if (!success) {
            return Result.failure(IllegalStateException("Failed to delete user"))
        }

        return Result.success(Unit)
    }
}

