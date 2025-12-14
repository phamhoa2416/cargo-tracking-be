package com.example.cargotracker.infrastructure.persistence.user

import com.example.cargotracker.infrastructure.persistence.user.entity.UserEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

/**
 * Spring Data R2DBC Repository using Coroutines
 * This is the concrete implementation using Spring Data CoroutineCrudRepository
 */

interface UserR2dbcRepository : CoroutineCrudRepository<UserEntity, UUID> {
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun findByUsername(username: String): UserEntity?
    suspend fun existsByEmail(email: String): Boolean
    suspend fun existsByUsername(username: String): Boolean
    @Query("""
        UPDATE users
        SET password_hash = :passwordHash,
            updated_at = :updatedAt
        WHERE id = :id
    """)
    suspend fun updatePasswordHashById(
        id: UUID,
        passwordHash: String,
        updatedAt: Instant
    ): Int
}
