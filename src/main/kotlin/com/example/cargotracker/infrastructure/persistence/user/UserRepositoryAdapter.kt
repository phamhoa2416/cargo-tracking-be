package com.example.cargotracker.infrastructure.persistence.user

import com.example.cargotracker.domain.user.entity.PasswordResetToken
import com.example.cargotracker.domain.user.entity.User
import com.example.cargotracker.domain.user.repository.UserRepository
import com.example.cargotracker.infrastructure.persistence.user.mapper.UserEntityMapper
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserRepositoryAdapter(
    private val userR2dbcRepository: UserR2dbcRepository
) : UserRepository {

    override suspend fun create(user: User): User {
        val entity = UserEntityMapper.toEntity(user)
        val savedEntity = userR2dbcRepository.save(entity)
        return UserEntityMapper.toDomain(savedEntity)
    }

    override suspend fun getByEmail(email: String): User? {
        val entity = userR2dbcRepository.findByEmail(email) ?: return null
        return UserEntityMapper.toDomain(entity)
    }

    override suspend fun getById(userId: UUID): User? {
        val entity = userR2dbcRepository.findById(userId) ?: return null
        return UserEntityMapper.toDomain(entity)
    }

    override suspend fun getAll(): List<User> {
        return userR2dbcRepository.findAll()
            .toList()
            .map { entity ->
                UserEntityMapper.toDomain(entity)
            }
    }

    override suspend fun update(user: User): Boolean {
        val exists = userR2dbcRepository.existsById(user.id)
        if (!exists) return false

        userR2dbcRepository.save(UserEntityMapper.toEntity(user))
        return true
    }

    override suspend fun updatePassword(userId: UUID, passwordHash: String): Boolean {
        val updatedAt = java.time.Instant.now()
        val rowsAffected = userR2dbcRepository.updatePasswordHashById(userId, passwordHash, updatedAt)
        return rowsAffected > 0
    }

    override suspend fun delete(userId: UUID): Boolean {
        val exists = userR2dbcRepository.existsById(userId)
        if (!exists) return false

        userR2dbcRepository.deleteById(userId)
        return true
    }

    override suspend fun createPasswordResetToken(token: PasswordResetToken): PasswordResetToken {
        // TODO: Implement when PasswordResetTokenEntity is created
        throw NotImplementedError("PasswordResetToken persistence not yet implemented")
    }

    override suspend fun getPasswordResetToken(token: String): PasswordResetToken? {
        // TODO: Implement when PasswordResetTokenEntity is created
        throw NotImplementedError("PasswordResetToken persistence not yet implemented")
    }

    override suspend fun markTokenAsUsed(tokenId: UUID): Boolean {
        // TODO: Implement when PasswordResetTokenEntity is created
        throw NotImplementedError("PasswordResetToken persistence not yet implemented")
    }
}
