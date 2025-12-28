package com.example.cargotracking.modules.shipment.model.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class CreateShipmentRequest(
    @field:NotNull(message = "Provider ID is required")
    val providerId: UUID,

    @field:NotBlank(message = "Goods description is required")
    @field:Size(min = 10, max = 1000, message = "Goods description must be 10-1000 characters")
    val goodsDescription: String,

    @field:NotBlank(message = "Pickup address is required")
    @field:Size(min = 10, max = 500, message = "Pickup address must be 10-500 characters")
    val pickupAddress: String,

    @field:NotBlank(message = "Delivery address is required")
    @field:Size(min = 10, max = 500, message = "Delivery address must be 10-500 characters")
    val deliveryAddress: String,

    val estimatedDeliveryAt: Instant? = null
)