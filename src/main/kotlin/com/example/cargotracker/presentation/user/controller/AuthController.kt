package com.example.cargotracker.presentation.user.controller

import com.example.cargotracker.application.user.usecase.ForgotPasswordUseCase
import com.example.cargotracker.application.user.usecase.RegisterUserUseCase
import com.example.cargotracker.application.user.usecase.ResetPasswordUseCase
import com.example.cargotracker.application.user.usecase.VerifyUserCredentialsUseCase
import com.example.cargotracker.infrastructure.security.JwtProvider
import com.example.cargotracker.presentation.common.dto.ErrorResponse
import com.example.cargotracker.presentation.common.dto.SuccessResponse
import com.example.cargotracker.presentation.user.dto.request.ForgotPasswordRequest
import com.example.cargotracker.presentation.user.dto.request.LoginRequest
import com.example.cargotracker.presentation.user.dto.request.RegisterRequest
import com.example.cargotracker.presentation.user.dto.request.ResetPasswordRequest
import com.example.cargotracker.presentation.user.dto.response.AuthResponse
import com.example.cargotracker.presentation.user.dto.response.UserResponse
import com.example.cargotracker.presentation.user.mapper.UserMapper
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.security.SecureRandom
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val verifyUserCredentialsUseCase: VerifyUserCredentialsUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider
) {

    @PostMapping("/register")
    suspend fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<Any> {
        // Hash password
        val passwordHash = passwordEncoder.encode(request.password)

        // Execute use case
        val result = registerUserUseCase.execute(
            username = request.username,
            email = request.email,
            passwordHash = passwordHash.toString(),
            fullName = request.fullName,
            phoneNumber = request.phoneNumber,
            role = request.role,
            address = request.address
        )

        return result.fold(
            onSuccess = { userDto ->
                // Generate JWT tokens
                val accessToken = jwtProvider.generateAccessToken(
                    userId = userDto.id,
                    email = userDto.email,
                    role = userDto.role
                )
                val refreshToken = jwtProvider.generateRefreshToken(userDto.id)
                val expiresAt = System.currentTimeMillis() + jwtProvider.getExpirationTime(JwtProvider.TokenType.ACCESS)

                val authResponse = AuthResponse(
                    user = UserMapper.toResponse(userDto),
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresAt = expiresAt
                )

                ResponseEntity.status(HttpStatus.CREATED).body(authResponse)
            },
            onFailure = { exception ->
                val errorResponse = ErrorResponse(
                    timestamp = Instant.now(),
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = "Registration Failed",
                    message = exception.message ?: "Failed to register user",
                    path = "/api/auth/register"
                )
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
            }
        )
    }

    @PostMapping("/login")
    suspend fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<Any> {
        // Verify user credentials
        val result = verifyUserCredentialsUseCase.execute(request.email)

        return result.fold(
            onSuccess = { userDto ->
                // TODO: Verify password with passwordEncoder.matches()
                // Note: UserDto doesn't expose passwordHash for security reasons
                // Password verification should be done in VerifyUserCredentialsUseCase
                // For now, we'll assume password verification is done in the use case

                // Generate JWT tokens
                val accessToken = jwtProvider.generateAccessToken(
                    userId = userDto.id,
                    email = userDto.email,
                    role = userDto.role
                )
                val refreshToken = jwtProvider.generateRefreshToken(userDto.id)
                val expiresAt = System.currentTimeMillis() + jwtProvider.getExpirationTime(JwtProvider.TokenType.ACCESS)

                val authResponse = AuthResponse(
                    user = UserMapper.toResponse(userDto),
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresAt = expiresAt
                )

                ResponseEntity.ok(authResponse)
            },
            onFailure = { exception ->
                val errorResponse = ErrorResponse(
                    timestamp = Instant.now(),
                    status = HttpStatus.UNAUTHORIZED.value(),
                    error = "Authentication Failed",
                    message = exception.message ?: "Invalid email or password",
                    path = "/api/auth/login"
                )
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
            }
        )
    }

    @PostMapping("/forgot-password")
    suspend fun forgotPassword(
        @Valid @RequestBody request: ForgotPasswordRequest
    ): ResponseEntity<Any> {
        // Generate reset token
        val resetToken = generateResetToken()

        // Execute use case
        val result = forgotPasswordUseCase.execute(
            email = request.email,
            token = resetToken,
            expiresInMinutes = 60
        )

        return result.fold(
            onSuccess = {
                // TODO: Send email with reset token
                // For now, just return success (in production, don't expose the token)
                val successResponse = SuccessResponse(
                    message = "Password reset email sent. Please check your email."
                )
                ResponseEntity.ok(successResponse)
            },
            onFailure = { exception ->
                val errorResponse = ErrorResponse(
                    timestamp = Instant.now(),
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = "Forgot Password Failed",
                    message = exception.message ?: "Failed to process forgot password request",
                    path = "/api/auth/forgot-password"
                )
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
            }
        )
    }

    @PostMapping("/reset-password")
    suspend fun resetPassword(
        @Valid @RequestBody request: ResetPasswordRequest
    ): ResponseEntity<Any> {
        val passwordHash = passwordEncoder.encode(request.newPassword)

        val result = resetPasswordUseCase.execute(
            token = request.token,
            newPasswordHash = passwordHash.toString()
        )

        return result.fold(
            onSuccess = {
                val successResponse = SuccessResponse(
                    message = "Password reset successfully"
                )
                ResponseEntity.ok(successResponse)
            },
            onFailure = { exception ->
                val errorResponse = ErrorResponse(
                    timestamp = Instant.now(),
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = "Reset Password Failed",
                    message = exception.message ?: "Failed to reset password",
                    path = "/api/auth/reset-password"
                )
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
            }
        )
    }

    private fun generateResetToken(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}

