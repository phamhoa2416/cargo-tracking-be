package com.example.cargotracker.application.user.usecase

import com.example.cargotracker.application.user.dto.UserDto
import com.example.cargotracker.application.user.mapper.UserMapper
import com.example.cargotracker.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class GetUserByEmailUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(email: String): Result<UserDto> {
        val user = userRepository.getByEmail(email)
            ?: return Result.failure(NoSuchElementException("User not found with email: $email"))

        if (!user.isActive) {
            return Result.failure(IllegalStateException("User account is inactive"))
        }

        return Result.success(UserMapper.toDto(user))
    }
}

