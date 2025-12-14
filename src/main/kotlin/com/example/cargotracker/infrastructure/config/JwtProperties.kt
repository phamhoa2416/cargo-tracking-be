package com.example.cargotracker.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessTokenExpiration: Long = 3600000, // 1 hour
    val refreshTokenExpiration: Long = 86400000 // 24 hours
)

