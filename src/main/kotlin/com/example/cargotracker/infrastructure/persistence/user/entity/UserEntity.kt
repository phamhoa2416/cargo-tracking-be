package com.example.cargotracker.infrastructure.persistence.user.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("users")
data class UserEntity(
    @Id
    @Column("id")
    val id: UUID,

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
    val role: String,

    @Column("address")
    val address: String? = null,

    @Column("is_active")
    val isActive: Boolean = true,

    @Column("created_at")
    val createdAt: Instant,

    @Column("updated_at")
    val updatedAt: Instant
)