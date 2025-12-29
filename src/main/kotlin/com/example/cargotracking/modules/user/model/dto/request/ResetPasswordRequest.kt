package com.example.cargotracking.modules.user.model.dto.request

import com.example.cargotracking.modules.user.validation.PasswordMatch
import com.example.cargotracking.modules.user.validation.StrongPassword
import jakarta.validation.constraints.NotBlank

@PasswordMatch(
    passwordField = "newPassword",
    confirmPasswordField = "confirmPassword",
    message = "Passwords do not match"
)
data class ResetPasswordRequest(
    @field:NotBlank(message = "Reset token is required")
    val token: String,

    @field:NotBlank(message = "New password is required")
//    @field:StrongPassword TODO: enable after demo
    val newPassword: String,

    @field:NotBlank(message = "Password confirmation is required")
    val confirmPassword: String
)
