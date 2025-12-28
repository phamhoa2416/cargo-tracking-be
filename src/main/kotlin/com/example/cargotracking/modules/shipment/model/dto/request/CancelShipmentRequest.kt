package com.example.cargotracking.modules.shipment.model.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CancelShipmentRequest(
    @field:NotBlank(message = "Cancellation reason is required")
    @field:Size(min = 10, max = 500, message = "Reason must be 10-500 characters")
    val reason: String
)
