package com.example.cargotracker.presentation.user.dto.request

import com.example.cargotracker.presentation.common.validation.PasswordMatch
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@PasswordMatch(passwordField = "newPassword", confirmPasswordField = "confirmPassword")
data class ResetPasswordRequest(
    @field:NotBlank
    val token: String,

    @field:NotBlank
    @field:Size(min = 8)
    val newPassword: String,

    @field:NotBlank
    val confirmPassword: String
)

