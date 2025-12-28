package com.example.cargotracking.modules.shipment.model.dto.request

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AssignDeviceRequest(
    @field:NotNull(message = "Device ID is required")
    val deviceId: UUID
)
