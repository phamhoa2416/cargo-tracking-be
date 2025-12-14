package com.example.cargotracker.presentation.user.controller

import com.example.cargotracker.application.user.usecase.*
import com.example.cargotracker.presentation.common.dto.ErrorResponse
import com.example.cargotracker.presentation.common.dto.SuccessResponse
import com.example.cargotracker.presentation.user.dto.request.ChangePasswordRequest
import com.example.cargotracker.presentation.user.dto.request.UpdateProfileRequest
import com.example.cargotracker.presentation.user.mapper.UserMapper
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val passwordEncoder: PasswordEncoder
) {

    @GetMapping("/{id}")
    suspend fun getUserById(
        @PathVariable id: UUID
    ): ResponseEntity<Any> {
        val result = getUserByIdUseCase.execute(id)

        return result.fold(
            onSuccess = { userDto ->
                val userResponse = UserMapper.toResponse(userDto)
                ResponseEntity.ok(userResponse)
            },
            onFailure = { exception ->
                val errorResponse = ErrorResponse(
                    timestamp = Instant.now(),
                    status = HttpStatus.NOT_FOUND.value(),
                    error = "User Not Found",
                    message = exception.message ?: "User not found",
                    path = "/api/users/$id"
                )
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
            }
        )
    }

    @GetMapping("/email/{email}")
    suspend fun getUserByEmail(
        @PathVariable email: String
    ): ResponseEntity<Any> {
        val result = getUserByEmailUseCase.execute(email)

        return result.fold(
            onSuccess = { userDto ->
                val userResponse = UserMapper.toResponse(userDto)
                ResponseEntity.ok(userResponse)
            },
            onFailure = { exception ->
                val errorResponse = ErrorResponse(
                    timestamp = Instant.now(),
                    status = HttpStatus.NOT_FOUND.value(),
                    error = "User Not Found",
                    message = exception.message ?: "User not found",
                    path = "/api/users/email/$email"
                )
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
            }
        )
    }

    @PutMapping("/{id}/profile")
    suspend fun updateProfile(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<Any> {
        val result = updateProfileUseCase.execute(
            userId = id,
            fullName = request.fullName,
            phoneNumber = request.phoneNumber,
            address = request.address
        )

        return result.fold(
            onSuccess = { userDto ->
                val userResponse = UserMapper.toResponse(userDto)
                ResponseEntity.ok(userResponse)
            },
            onFailure = { exception ->
                val errorResponse = ErrorResponse(
                    timestamp = Instant.now(),
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = "Update Profile Failed",
                    message = exception.message ?: "Failed to update profile",
                    path = "/api/users/$id/profile"
                )
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
            }
        )
    }

    @PutMapping("/{id}/password")
    suspend fun changePassword(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<Any> {
        // TODO: Verify old password
        // For now, we'll assume old password verification is done in security layer
        // In production: passwordEncoder.matches(request.oldPassword, currentPasswordHash)

        // Hash new password
        val newPasswordHash = passwordEncoder.encode(request.newPassword)

        // Execute use case
        val result = changePasswordUseCase.execute(
            userId = id,
            newPasswordHash = newPasswordHash.toString()
        )

        return result.fold(
            onSuccess = {
                val successResponse = SuccessResponse(
                    message = "Password changed successfully"
                )
                ResponseEntity.ok(successResponse)
            },
            onFailure = { exception ->
                val errorResponse = ErrorResponse(
                    timestamp = Instant.now(),
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = "Change Password Failed",
                    message = exception.message ?: "Failed to change password",
                    path = "/api/users/$id/password"
                )
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
            }
        )
    }

    @DeleteMapping("/{id}")
    suspend fun deleteUser(
        @PathVariable id: UUID
    ): ResponseEntity<Any> {
        val result = deleteUserUseCase.execute(id)

        return result.fold(
            onSuccess = {
                val successResponse = SuccessResponse(
                    message = "User deleted successfully"
                )
                ResponseEntity.ok(successResponse)
            },
            onFailure = { exception ->
                val errorResponse = ErrorResponse(
                    timestamp = Instant.now(),
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = "Delete User Failed",
                    message = exception.message ?: "Failed to delete user",
                    path = "/api/users/$id"
                )
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
            }
        )
    }
}

