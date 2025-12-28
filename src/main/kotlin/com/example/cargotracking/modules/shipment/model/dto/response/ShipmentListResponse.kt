package com.example.cargotracking.modules.shipment.model.dto.response

data class ShipmentListResponse(
    val shipments: List<ShipmentResponse>,
    val total: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)
