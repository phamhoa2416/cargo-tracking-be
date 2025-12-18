package com.example.cargotracker.presentation.user.controller

import com.example.cargotracker.presentation.common.dto.ErrorResponse
import com.example.cargotracker.presentation.common.dto.SuccessResponse
import com.example.cargotracker.presentation.user.dto.request.ChangePasswordRequest
import com.example.cargotracker.presentation.user.dto.request.UpdateProfileRequest
import com.example.cargotracker.presentation.user.dto.response.UserResponse
import com.example.cargotracker.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/{id}")
    suspend fun getUserById(
        @PathVariable id: UUID
    ): ResponseEntity<Any> {
        return try {
            val user = userService.getUserById(id)
            val response = UserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                fullName = user.fullName,
                phoneNumber = user.phoneNumber,
                role = com.example.cargotracker.domain.user.types.UserRole.valueOf(user.role),
                defaultAddress = user.address,
                isActive = user.isActive,
                createdAt = user.createdAt
            )
            ResponseEntity.ok(response)
        } catch (ex: NoSuchElementException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.NOT_FOUND.value(),
                error = "User Not Found",
                message = ex.message ?: "User not found",
                path = "/api/users/$id"
            )
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
        } catch (ex: IllegalStateException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "User Not Available",
                message = ex.message ?: "User account is inactive",
                path = "/api/users/$id"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }

    @GetMapping("/email/{email}")
    suspend fun getUserByEmail(
        @PathVariable email: String
    ): ResponseEntity<Any> {
        return try {
            val user = userService.getUserByEmail(email)
            val response = UserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                fullName = user.fullName,
                phoneNumber = user.phoneNumber,
                role = com.example.cargotracker.domain.user.types.UserRole.valueOf(user.role),
                defaultAddress = user.address,
                isActive = user.isActive,
                createdAt = user.createdAt
            )
            ResponseEntity.ok(response)
        } catch (ex: NoSuchElementException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.NOT_FOUND.value(),
                error = "User Not Found",
                message = ex.message ?: "User not found",
                path = "/api/users/email/$email"
            )
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
        } catch (ex: IllegalStateException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "User Not Available",
                message = ex.message ?: "User account is inactive",
                path = "/api/users/email/$email"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }

    @PutMapping("/{id}/profile")
    suspend fun updateProfile(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<Any> {
        return try {
            val user = userService.updateProfile(
                id = id,
                fullName = request.fullName,
                phoneNumber = request.phoneNumber,
                address = request.address
            )
            val response = UserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                fullName = user.fullName,
                phoneNumber = user.phoneNumber,
                role = com.example.cargotracker.domain.user.types.UserRole.valueOf(user.role),
                defaultAddress = user.address,
                isActive = user.isActive,
                createdAt = user.createdAt
            )
            ResponseEntity.ok(response)
        } catch (ex: NoSuchElementException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.NOT_FOUND.value(),
                error = "Update Profile Failed",
                message = ex.message ?: "User not found",
                path = "/api/users/$id/profile"
            )
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
        } catch (ex: IllegalStateException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Update Profile Failed",
                message = ex.message ?: "Failed to update profile",
                path = "/api/users/$id/profile"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }

    @PutMapping("/{id}/password")
    suspend fun changePassword(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<Any> {
        return try {
            userService.changePassword(
                id = id,
                oldPassword = request.oldPassword,
                newPassword = request.newPassword
            )
            val successResponse = SuccessResponse(
                message = "Password changed successfully"
            )
            ResponseEntity.ok(successResponse)
        } catch (ex: NoSuchElementException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.NOT_FOUND.value(),
                error = "Change Password Failed",
                message = ex.message ?: "User not found",
                path = "/api/users/$id/password"
            )
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
        } catch (ex: IllegalArgumentException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Change Password Failed",
                message = ex.message ?: "Old password is incorrect",
                path = "/api/users/$id/password"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        } catch (ex: IllegalStateException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Change Password Failed",
                message = ex.message ?: "Failed to change password",
                path = "/api/users/$id/password"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }

    @DeleteMapping("/{id}")
    suspend fun deleteUser(
        @PathVariable id: UUID
    ): ResponseEntity<Any> {
        return try {
            userService.deleteUser(id)
            val successResponse = SuccessResponse(
                message = "User deleted successfully"
            )
            ResponseEntity.ok(successResponse)
        } catch (ex: NoSuchElementException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.NOT_FOUND.value(),
                error = "Delete User Failed",
                message = ex.message ?: "User not found",
                path = "/api/users/$id"
            )
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
        } catch (ex: Exception) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Delete User Failed",
                message = ex.message ?: "Failed to delete user",
                path = "/api/users/$id"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }
}

