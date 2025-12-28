package com.example.cargotracking.modules.shipment.model.dto.response

import com.example.cargotracking.modules.shipment.model.entity.Shipment
import com.example.cargotracking.modules.shipment.model.types.ShipmentStatus
import java.time.Instant
import java.util.UUID

data class ShipmentResponse(
    val id: UUID,
    val status: ShipmentStatus,

    val customerId: UUID,
    val providerId: UUID,
    val shipperId: UUID?,
    val deviceId: UUID?,

    val goodsDescription: String,

    val pickupAddress: String,
    val deliveryAddress: String,

    val estimatedDeliveryAt: Instant?,
    val actualDeliveryAt: Instant?,

    val isDelayed: Boolean,

    val createdAt: Instant?,
    val updatedAt: Instant?
) {
    companion object {
        fun from(shipment: Shipment): ShipmentResponse {
            return ShipmentResponse(
                id = shipment.id,
                status = shipment.status,
                customerId = shipment.customerId,
                providerId = shipment.providerId,
                shipperId = shipment.shipperId,
                deviceId = shipment.deviceId,
                goodsDescription = shipment.goodsDescription,
                pickupAddress = shipment.pickupAddress,
                deliveryAddress = shipment.deliveryAddress,
                estimatedDeliveryAt = shipment.estimatedDeliveryAt,
                actualDeliveryAt = shipment.actualDeliveryAt,
                isDelayed = calculateDelay(shipment),
                createdAt = shipment.createdAt,
                updatedAt = shipment.updatedAt
            )
        }

        private fun calculateDelay(shipment: Shipment): Boolean {
            val estimatedDelivery = shipment.estimatedDeliveryAt ?: return false

            return when {
                shipment.actualDeliveryAt != null ->
                    shipment.actualDeliveryAt!!.isAfter(estimatedDelivery)

                shipment.status == ShipmentStatus.IN_TRANSIT ->
                    Instant.now().isAfter(estimatedDelivery)

                else -> false
            }
        }
    }
}