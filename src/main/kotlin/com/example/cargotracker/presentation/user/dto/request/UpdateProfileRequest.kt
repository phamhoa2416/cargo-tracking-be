package com.example.cargotracker.presentation.user.dto.request

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateProfileRequest(
    @field:Size(min = 2, max = 255)
    val fullName: String? = null,

    @field:Pattern(
        regexp = "^[0-9+()\\s-]{8,20}$",
        message = "Invalid phone number"
    )
    val phoneNumber: String? = null,

    @field:Size(max = 500)
    val address: String? = null
)

