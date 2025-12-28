package com.example.cargotracking.modules.shipment.model.dto.request

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AssignShipperRequest(
    @field:NotNull(message = "Shipper ID is required")
    val shipperId: UUID
)
