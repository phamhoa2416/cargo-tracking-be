package com.example.cargotracker.domain.user.repository

import com.example.cargotracker.domain.user.entity.RefreshToken
import java.time.Duration
import java.time.Instant
import java.util.UUID

interface RefreshTokenRepository {
    suspend fun create(token: RefreshToken)
    suspend fun getByToken(token: String): RefreshToken?
    suspend fun revoke(tokenId: UUID)
    suspend fun revokeAllUserTokens(userId: UUID)
    suspend fun deleteExpiredTokens(olderThan: Duration)
    suspend fun getUserTokens(userId: UUID): List<RefreshToken>
}