package com.example.cargotracker.application.user.usecase

import com.example.cargotracker.application.user.dto.UserDto
import com.example.cargotracker.application.user.mapper.UserMapper
import com.example.cargotracker.domain.user.entity.User
import com.example.cargotracker.domain.user.repository.UserRepository
import com.example.cargotracker.domain.user.types.UserRole
import org.springframework.stereotype.Service

@Service
class RegisterUserUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(
        username: String,
        email: String,
        passwordHash: String,
        fullName: String,
        phoneNumber: String?,
        role: UserRole,
        address: String?
    ): Result<UserDto> {
        val existingUserByEmail = userRepository.getByEmail(email)
        if (existingUserByEmail != null) {
            return Result.failure(IllegalArgumentException("Email already exists"))
        }

        val user = User(
            username = username,
            email = email,
            passwordHash = passwordHash,
            fullName = fullName,
            phoneNumber = phoneNumber,
            role = role,
            address = address
        )

        return try {
            val savedUser = userRepository.create(user)
            Result.success(UserMapper.toDto(savedUser))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

