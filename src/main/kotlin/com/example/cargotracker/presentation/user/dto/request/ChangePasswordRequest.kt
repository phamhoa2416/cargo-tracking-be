package com.example.cargotracker.presentation.user.dto.request

import com.example.cargotracker.presentation.common.validation.PasswordMatch
import com.example.cargotracker.presentation.common.validation.PasswordNotMatch
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@PasswordMatch(passwordField = "newPassword", confirmPasswordField = "confirmPassword")
@PasswordNotMatch(oldPasswordField = "oldPassword", newPasswordField = "newPassword")
data class ChangePasswordRequest(
    @field:NotBlank
    val oldPassword: String,

    @field:NotBlank
    @field:Size(min = 8)
    val newPassword: String,

    @field:NotBlank
    val confirmPassword: String
)

