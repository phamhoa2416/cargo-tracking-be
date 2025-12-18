package com.example.cargotracker.presentation.user.controller

import com.example.cargotracker.presentation.common.dto.ErrorResponse
import com.example.cargotracker.presentation.common.dto.SuccessResponse
import com.example.cargotracker.presentation.user.dto.request.ForgotPasswordRequest
import com.example.cargotracker.presentation.user.dto.request.LoginRequest
import com.example.cargotracker.presentation.user.dto.request.RegisterRequest
import com.example.cargotracker.presentation.user.dto.request.ResetPasswordRequest
import com.example.cargotracker.user.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    suspend fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<Any> {
        return try {
            val authResponse = authService.register(request)
            ResponseEntity.status(HttpStatus.CREATED).body(authResponse)
        } catch (ex: IllegalArgumentException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Registration Failed",
                message = ex.message ?: "Failed to register user",
                path = "/api/auth/register"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }

    @PostMapping("/login")
    suspend fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<Any> {
        return try {
            val authResponse = authService.login(request)
            ResponseEntity.ok(authResponse)
        } catch (ex: IllegalArgumentException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.UNAUTHORIZED.value(),
                error = "Authentication Failed",
                message = ex.message ?: "Invalid email or password",
                path = "/api/auth/login"
            )
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
        } catch (ex: IllegalStateException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.FORBIDDEN.value(),
                error = "Authentication Failed",
                message = ex.message ?: "User account is inactive",
                path = "/api/auth/login"
            )
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
        }
    }

    @PostMapping("/forgot-password")
    suspend fun forgotPassword(
        @Valid @RequestBody request: ForgotPasswordRequest
    ): ResponseEntity<Any> {
        return try {
            authService.forgotPassword(request)
            val successResponse = SuccessResponse(
                message = "Password reset email sent. Please check your email."
            )
            ResponseEntity.ok(successResponse)
        } catch (ex: NoSuchElementException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Forgot Password Failed",
                message = ex.message ?: "Failed to process forgot password request",
                path = "/api/auth/forgot-password"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        } catch (ex: IllegalStateException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Forgot Password Failed",
                message = ex.message ?: "Failed to process forgot password request",
                path = "/api/auth/forgot-password"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }

    @PostMapping("/reset-password")
    suspend fun resetPassword(
        @Valid @RequestBody request: ResetPasswordRequest
    ): ResponseEntity<Any> {
        return try {
            authService.resetPassword(request)
            val successResponse = SuccessResponse(
                message = "Password reset successfully"
            )
            ResponseEntity.ok(successResponse)
        } catch (ex: UnsupportedOperationException) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.NOT_IMPLEMENTED.value(),
                error = "Reset Password Failed",
                message = ex.message ?: "Reset password is not implemented",
                path = "/api/auth/reset-password"
            )
            ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(errorResponse)
        } catch (ex: Exception) {
            val errorResponse = ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Reset Password Failed",
                message = ex.message ?: "Failed to reset password",
                path = "/api/auth/reset-password"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }
    }
}
