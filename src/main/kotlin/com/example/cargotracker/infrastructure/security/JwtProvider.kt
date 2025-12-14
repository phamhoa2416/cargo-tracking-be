package com.example.cargotracker.infrastructure.security

import com.example.cargotracker.domain.user.types.UserRole
import com.example.cargotracker.infrastructure.config.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties
) {
    private val secretKey = Keys.hmacShaKeyFor(
        jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
    )

    fun validate(token: String): Mono<JwtClaims> {
        return try {
            val claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload

            val userId = UUID.fromString(claims.get("userId", String::class.java))
            val email = claims.get("email", String::class.java)
            val role = UserRole.valueOf(claims.get("role", String::class.java))

            Mono.just(JwtClaims(userId, email, role))
        } catch (e: Exception) {
            Mono.error(IllegalArgumentException("Invalid JWT token: ${e.message}", e))
        }
    }

    fun generateAccessToken(userId: UUID, email: String, role: UserRole): String {
        val now = Date()
        val expiration = Date(now.time + jwtProperties.accessTokenExpiration)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("userId", userId.toString())
            .claim("email", email)
            .claim("role", role.name)
            .claim("type", "access")
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun generateRefreshToken(userId: UUID): String {
        val now = Date()
        val expiration = Date(now.time + jwtProperties.refreshTokenExpiration)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("userId", userId.toString())
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun getExpirationTime(tokenType: TokenType): Long {
        return when (tokenType) {
            TokenType.ACCESS -> jwtProperties.accessTokenExpiration
            TokenType.REFRESH -> jwtProperties.refreshTokenExpiration
        }
    }

    enum class TokenType {
        ACCESS, REFRESH
    }
}
