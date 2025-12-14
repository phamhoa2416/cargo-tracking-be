package com.example.cargotracker.domain.user.entity

import com.example.cargotracker.domain.common.entity.BaseEntity
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("password_reset_tokens")
class PasswordResetToken(
    id: UUID = UUID.randomUUID(),
    createdAt: java.time.Instant? = null,
    updatedAt: java.time.Instant? = null,
    @Column("user_id")
    val userId: UUID,

    @Column("token")
    val token: String,

    @Column("expires_at")
    val expiresAt: java.time.Instant,

    @Column("used")
    val used: Boolean = false
) : BaseEntity(id, createdAt, updatedAt) {

    init {
        validate()
    }

    override fun validate() {
        validateUserId()
        validateToken()
        validateExpiresAt()
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
            "Expires at must be in the future"
        }
    }

    fun isExpired(): Boolean {
        return expiresAt.isBefore(java.time.Instant.now())
    }

    fun isActive(): Boolean {
        return !used && !isExpired()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PasswordResetToken) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "PasswordResetToken(id=$id, userId=$userId, expiresAt=$expiresAt, used=$used)"
    }
}
