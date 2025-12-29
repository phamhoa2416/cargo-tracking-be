package com.example.cargotracking.modules.user.model.dto.request

import com.example.cargotracking.modules.user.validation.PasswordMatch
import com.example.cargotracking.modules.user.validation.PasswordNotMatch
import com.example.cargotracking.modules.user.validation.StrongPassword
import jakarta.validation.constraints.NotBlank

@PasswordMatch(
    passwordField = "newPassword",
    confirmPasswordField = "confirmPassword",
    message = "Confirm password must match new password"
)
@PasswordNotMatch(
    oldPasswordField = "currentPassword",
    newPasswordField = "newPassword",
    message = "New password must not match old password"
)
data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,

    @field:NotBlank(message = "New password is required")
//    @field:StrongPassword TODO: enable after demo
    val newPassword: String,

    @field:NotBlank(message = "Password confirmation is required")
    val confirmPassword: String
)