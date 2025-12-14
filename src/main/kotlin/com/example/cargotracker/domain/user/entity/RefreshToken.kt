package com.example.cargotracker.domain.user.entity

import com.example.cargotracker.domain.common.entity.BaseEntity
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("refresh_tokens")
class RefreshToken(
    id: UUID = UUID.randomUUID(),
    createdAt: java.time.Instant? = null,
    updatedAt: java.time.Instant? = null,
    @Column("user_id")
    val userId: UUID,

    @Column("token")
    val token: String,

    @Column("expires_at")
    val expiresAt: java.time.Instant,

    @Column("revoked")
    val revoked: Boolean = false,

    @Column("revoked_at")
    val revokedAt: java.time.Instant? = null
) : BaseEntity(id, createdAt, updatedAt) {

    init {
        validate()
    }

    override fun validate() {
        validateUserId()
        validateToken()
        validateExpiresAt()
        validateRevokedState()
    }

    private fun validateUserId() {
        require(userId != UUID(0, 0)) { "User ID cannot be null or empty" }
    }

    private fun validateToken() {
        require(token.isNotBlank()) { "Token cannot be blank" }
        require(token.length >= 32) { "Token must be at least 32 characters long" }
    }

    private fun validateExpiresAt() {
        require(expiresAt.isAfter(java.time.Instant.now())) {
            "ExpiresAt must be in the future"
        }
    }

    private fun validateRevokedState() {
        if (revoked) {
            require(revokedAt != null) { "RevokedAt must be set when token is revoked" }
            require(revokedAt.isBefore(java.time.Instant.now()) || revokedAt == java.time.Instant.now()) {
                "RevokedAt cannot be in the future"
            }
        } else {
            require(revokedAt == null) { "RevokedAt must be null when token is not revoked" }
        }
    }

    fun isExpired(): Boolean {
        return expiresAt.isBefore(java.time.Instant.now())
    }

    fun isActive(): Boolean {
        return !revoked && !isExpired()
    }

    fun revoke(): RefreshToken {
        if (revoked) {
            return this
        }
        return RefreshToken(
            id = id,
            createdAt = createdAt,
            updatedAt = updatedAt,
            userId = userId,
            token = token,
            expiresAt = expiresAt,
            revoked = true,
            revokedAt = java.time.Instant.now()
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RefreshToken) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "RefreshToken(id=$id, userId=$userId, expiresAt=$expiresAt, revoked=$revoked)"
    }
}
