package com.example.cargotracker.domain.user.repository

import com.example.cargotracker.domain.user.entity.PasswordResetToken
import com.example.cargotracker.domain.user.entity.User
import java.util.UUID

interface UserRepository {
    suspend fun create(user: User): User
    suspend fun getByEmail(email: String): User?
    suspend fun getById(userId: UUID): User?
    suspend fun getAll(): List<User>
    suspend fun update(user: User): Boolean
    suspend fun updatePassword(userId: UUID, passwordHash: String): Boolean
    suspend fun delete(userId: UUID): Boolean

    suspend fun createPasswordResetToken(token: PasswordResetToken): PasswordResetToken
    suspend fun getPasswordResetToken(token: String): PasswordResetToken?
    suspend fun markTokenAsUsed(tokenId: UUID): Boolean
}