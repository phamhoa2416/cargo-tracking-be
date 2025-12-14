package com.example.cargotracker.presentation.user.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ForgotPasswordRequest(
    @field:NotBlank
    @field:Email
    val email: String
)

