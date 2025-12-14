package com.example.cargotracker.application.user.usecase

import com.example.cargotracker.application.user.dto.UserDto
import com.example.cargotracker.application.user.mapper.UserMapper
import com.example.cargotracker.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetUserByIdUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(userId: UUID): Result<UserDto> {
        val user = userRepository.getById(userId)
            ?: return Result.failure(NoSuchElementException("User not found with ID: $userId"))

        if (!user.isActive) {
            return Result.failure(IllegalStateException("User account is inactive"))
        }

        return Result.success(UserMapper.toDto(user))
    }
}

