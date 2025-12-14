package com.example.cargotracker.infrastructure.persistence.user.mapper

import com.example.cargotracker.domain.user.entity.User
import com.example.cargotracker.domain.user.types.UserRole
import com.example.cargotracker.infrastructure.persistence.user.entity.UserEntity
import java.time.Instant

/**
 * Mapper between UserEntity (infrastructure) and User (domain)
 */
object UserEntityMapper {
    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            username = entity.username,
            email = entity.email,
            passwordHash = entity.passwordHash,
            fullName = entity.fullName,
            phoneNumber = entity.phoneNumber,
            role = UserRole.valueOf(entity.role),
            address = entity.address,
            isActive = entity.isActive
        )
    }

    fun toEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            username = user.username,
            email = user.email,
            passwordHash = user.passwordHash,
            fullName = user.fullName,
            phoneNumber = user.phoneNumber,
            role = user.role.name,
            address = user.address,
            isActive = user.isActive,
            createdAt = user.createdAt ?: Instant.now(),
            updatedAt = user.updatedAt ?: Instant.now()
        )
    }
}