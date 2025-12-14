package com.example.cargotracker.domain.user.entity

import com.example.cargotracker.domain.common.entity.BaseEntity
import com.example.cargotracker.domain.user.types.UserRole
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("users")
class User(
    id: UUID = UUID.randomUUID(),
    createdAt: java.time.Instant? = null,
    updatedAt: java.time.Instant? = null,
    @Column("username")
    val username: String,

    @Column("email")
    val email: String,

    @Column("password_hash")
    val passwordHash: String,

    @Column("full_name")
    val fullName: String,

    @Column("phone_number")
    val phoneNumber: String? = null,

    @Column("role")
    val role: UserRole,

    @Column("address")
    val address: String? = null,

    @Column("is_active")
    val isActive: Boolean = true
) : BaseEntity(id, createdAt, updatedAt) {

    init {
        validate()
    }

    companion object {
        private val EMAIL_REGEX = Regex(
            "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$",
            RegexOption.IGNORE_CASE
        )

        private val PHONE_REGEX = Regex("^[0-9+()\\s-]{8,20}$")
    }

    override fun validate() {
        validateUsername()
        validateEmail()
        validatePassword()
        validateFullName()
        validatePhoneNumber()
        validateAddress()
    }

    private fun validateUsername() {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(username.length in 3..100) {
            "Username must be between 3 and 100 characters"
        }
    }

    private fun validateEmail() {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(EMAIL_REGEX.matches(email)) {
            "Email format is invalid"
        }
    }

    private fun validatePassword() {
        require(passwordHash.isNotBlank()) { "Password cannot be blank" }
        require(passwordHash.length >= 8) {
            "Password must be at least 8 characters long"
        }
    }

    private fun validateFullName() {
        require(fullName.isNotBlank()) { "Full name cannot be blank" }
        require(fullName.length in 2..255) {
            "Full name must be between 2 and 255 characters"
        }
    }

    private fun validatePhoneNumber() {
        phoneNumber?.let { phone ->
            require(phone.isNotBlank()) { "Phone number cannot be blank if provided" }
            require(PHONE_REGEX.matches(phone)) {
                "Phone number format is invalid. Must be 8-20 characters and contain only digits, +, (), spaces, or hyphens"
            }
        }
    }

    private fun validateAddress() {
        address?.let { addr ->
            require(addr.length <= 500) {
                "Address must not exceed 500 characters"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "User(id=$id, username='$username', email='$email', fullName='$fullName', role=$role, isActive=$isActive)"
    }
}

