package com.example.cargotracking.modules.shipment.model.entity

import com.example.cargotracking.common.entity.BaseEntity
import com.example.cargotracking.modules.shipment.model.types.ShipmentStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID


@Entity
@Table(
    name = "shipments",
    indexes = [
        Index(name = "idx_shipment_customer_id", columnList = "customer_id"),
        Index(name = "idx_shipment_provider_id", columnList = "provider_id"),
        Index(name = "idx_shipment_shipper_id", columnList = "shipper_id"),
        Index(name = "idx_shipment_device_id", columnList = "device_id"),
        Index(name = "idx_shipment_status", columnList = "status")
    ]
)
class Shipment private constructor(
    id: UUID,

    @Column(name = "customer_id", nullable = false)
    private var _customerId: UUID,

    @Column(name = "provider_id", nullable = false)
    private var _providerId: UUID,

    @Column(name = "shipper_id")
    private var _shipperId: UUID? = null,

    @Column(name = "device_id")
    private var _deviceId: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private var _status: ShipmentStatus = ShipmentStatus.PENDING,

    @Column(name = "goods_description", nullable = false, length = 1000)
    private var _goodsDescription: String,

    @Column(name = "pickup_address", nullable = false, length = 500)
    private var _pickupAddress: String,

    @Column(name = "delivery_address", nullable = false, length = 500)
    private var _deliveryAddress: String,

    @Column(name = "estimated_delivery_at")
    private var _estimatedDeliveryAt: Instant? = null,

    @Column(name = "actual_delivery_at")
    private var _actualDeliveryAt: Instant? = null

): BaseEntity(id) {
    protected constructor() : this(
        id = UUID.randomUUID(),
        _customerId = UUID.randomUUID(),
        _providerId = UUID.randomUUID(),
        _goodsDescription = "",
        _pickupAddress = "",
        _deliveryAddress = ""
    )

    val customerId: UUID get() = _customerId
    val providerId: UUID get() = _providerId
    val shipperId: UUID? get() = _shipperId
    val deviceId: UUID? get() = _deviceId
    val status: ShipmentStatus get() = _status
    val goodsDescription: String get() = _goodsDescription
    val pickupAddress: String get() = _pickupAddress
    val deliveryAddress: String get() = _deliveryAddress
    val estimatedDeliveryAt: Instant? get() = _estimatedDeliveryAt
    val actualDeliveryAt: Instant? get() = _actualDeliveryAt

    override fun validateInvariants() {
        check(_customerId != UUID(0, 0)) {
            "Customer ID must be valid"
        }

        check(_providerId != UUID(0, 0)) {
            "Provider ID must be valid"
        }

        check(_goodsDescription.isNotBlank() && _goodsDescription.length <= 1000) {
            "Goods description must be 1-1000 characters"
        }

        check(_pickupAddress.isNotBlank() && _pickupAddress.length <= 500) {
            "Pickup address must be 1-500 characters"
        }

        check(_deliveryAddress.isNotBlank() && _deliveryAddress.length <= 500) {
            "Delivery address must be 1-500 characters"
        }

        when (_status) {
            ShipmentStatus.ASSIGNED, ShipmentStatus.IN_TRANSIT -> {
                check(_shipperId != null) {
                    "ASSIGNED/IN_TRANSIT shipment must have shipper"
                }
                check(_deviceId != null) {
                    "ASSIGNED/IN_TRANSIT shipment must have device"
                }
            }
            ShipmentStatus.COMPLETED -> {
                check(_actualDeliveryAt != null) {
                    "COMPLETED shipment must have actual delivery time"
                }
            }
            else -> {}
        }
    }

    companion object {

        fun create(
            customerId: UUID,
            providerId: UUID,
            goodsDescription: String,
            pickupAddress: String,
            deliveryAddress: String,
            estimatedDeliveryAt: Instant? = null,
        ): Shipment {
            require(customerId != UUID(0, 0)) { "Customer ID must be valid" }
            require(providerId != UUID(0, 0)) { "Provider ID must be valid" }
            require(goodsDescription.isNotBlank()) { "Goods description is required" }
            require(pickupAddress.isNotBlank()) { "Pickup address is required" }
            require(deliveryAddress.isNotBlank()) { "Delivery address is required" }

            val shipment = Shipment(
                id = UUID.randomUUID(),
                _customerId = customerId,
                _providerId = providerId,
                _goodsDescription = goodsDescription.trim(),
                _pickupAddress = pickupAddress.trim(),
                _deliveryAddress = deliveryAddress.trim(),
                _estimatedDeliveryAt = estimatedDeliveryAt,
                _status = ShipmentStatus.PENDING
            )

            shipment.validateInvariants()
            return shipment
        }
    }

    fun assignShipper(shipperId: UUID) {
        require(_status == ShipmentStatus.PENDING) {
            "Only PENDING shipments can be assigned. Current status: $_status"
        }
        require(shipperId != UUID(0, 0)) { "Shipper ID must be valid" }

        _shipperId = shipperId
        _status = ShipmentStatus.ASSIGNED
    }

    fun assignDevice(deviceId: UUID) {
        require(_status == ShipmentStatus.ASSIGNED) {
            "Only ASSIGNED shipments can have device assigned. Current status: $_status"
        }
        require(deviceId != UUID(0, 0)) {
            "Device ID must be valid"
        }
        _deviceId = deviceId
    }

    fun startTransit() {
        require(_status == ShipmentStatus.ASSIGNED) {
            "Only ASSIGNED shipments can start transit. Current status: $_status"
        }
        require(_deviceId != null) {
            "Shipment must have a device assigned before starting transit"
        }
        _status = ShipmentStatus.IN_TRANSIT
    }

    fun unassignShipper() {
        require(_status == ShipmentStatus.ASSIGNED) {
            "Only ASSIGNED shipments can be unassigned"
        }
        _shipperId = null
        _status = ShipmentStatus.PENDING
    }


    fun complete(deliveredAt: Instant = Instant.now()) {
        require(_status == ShipmentStatus.IN_TRANSIT) {
            "Only IN_TRANSIT shipment can be completed"
        }
        _actualDeliveryAt = deliveredAt
        _status = ShipmentStatus.COMPLETED
    }

    fun cancel() {
        require(_status in listOf(
            ShipmentStatus.PENDING,
            ShipmentStatus.ASSIGNED,
            ShipmentStatus.IN_TRANSIT
        )) {
            "Cannot cancel shipment in status: $_status"
        }

        _status = ShipmentStatus.CANCELLED
    }

    fun updateGoodsDescription(description: String) {
        require(description.isNotBlank() && description.length <= 1000) {
            "Goods description must be 1-1000 characters"
        }
        require(_status == ShipmentStatus.PENDING) {
            "Only PENDING shipments can be updated. Current status: $_status"
        }
        _goodsDescription = description.trim()
    }

    fun updatePickupAddress(address: String) {
        require(address.isNotBlank() && address.length <= 500) {
            "Pickup address must be 1-500 characters"
        }
        require(_status == ShipmentStatus.PENDING) {
            "Only PENDING shipments can be updated. Current status: $_status"
        }
        _pickupAddress = address.trim()
    }

    fun updateDeliveryAddress(address: String) {
        require(address.isNotBlank() && address.length <= 500) {
            "Delivery address must be 1-500 characters"
        }
        require(_status == ShipmentStatus.PENDING) {
            "Only PENDING shipments can be updated. Current status: $_status"
        }
        _deliveryAddress = address.trim()
    }

    fun updateEstimatedDeliveryAt(estimatedDeliveryAt: Instant?) {
        require(_status == ShipmentStatus.PENDING) {
            "Only PENDING shipments can be updated. Current status: $_status"
        }
        _estimatedDeliveryAt = estimatedDeliveryAt
    }

    fun updateDetails(
        goodsDescription: String? = null,
        pickupAddress: String? = null,
        deliveryAddress: String? = null,
        estimatedDeliveryAt: Instant? = null
    ) {
        goodsDescription?.let { updateGoodsDescription(it) }
        pickupAddress?.let { updatePickupAddress(it) }
        deliveryAddress?.let { updateDeliveryAddress(it) }
        estimatedDeliveryAt?.let { updateEstimatedDeliveryAt(it) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Shipment) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "Shipment(id=$id, status=$_status, customer=$_customerId, provider=$_providerId)"
    }
}