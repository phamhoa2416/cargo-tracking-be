package com.example.cargotracking.modules.shipment.model.dto.request

import com.example.cargotracking.modules.shipment.model.types.ShipmentStatus
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.Instant
import java.util.UUID

data class ShipmentFilterRequest(
    val status: ShipmentStatus? = null,
    val customerId: UUID? = null,
    val providerId: UUID? = null,
    val shipperId: UUID? = null,
    val deviceId: UUID? = null,

    val createdAfter: Instant? = null,
    val createdBefore: Instant? = null,
    val deliveryAfter: Instant? = null,
    val deliveryBefore: Instant? = null,

    val search: String? = null,

    @field:Min(value = 1, message = "Page must be at least 1")
    val page: Int = 1,

    @field:Min(value = 1, message = "Page size must be at least 1")
    @field:Max(value = 100, message = "Page size must not exceed 100")
    val pageSize: Int = 20,

    val sortBy: String? = "createdAt",
    val sortOrder: String? = "desc"
)
