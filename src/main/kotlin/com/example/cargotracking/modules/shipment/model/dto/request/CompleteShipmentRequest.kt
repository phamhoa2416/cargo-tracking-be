package com.example.cargotracking.modules.shipment.model.dto.request

import java.time.Instant

data class CompleteShipmentRequest(
    val deliveredAt: Instant? = null
)