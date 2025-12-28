package com.example.cargotracking.modules.shipment.model.dto.request

import jakarta.validation.constraints.Size
import java.time.Instant

data class UpdateShipmentRequest(
    @field:Size(min = 10, max = 1000, message = "Goods description must be 10-1000 characters")
    val goodsDescription: String? = null,

    @field:Size(min = 10, max = 500, message = "Pickup address must be 10-500 characters")
    val pickupAddress: String? = null,

    @field:Size(min = 10, max = 500, message = "Delivery address must be 10-500 characters")
    val deliveryAddress: String? = null,

    val estimatedDeliveryAt: Instant? = null
)
