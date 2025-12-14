package com.example.cargotracker.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rate-limit")
data class RateLimitProperties(
    val default: Double = 10.0,
    val endpoints: String = "",
    val cleanupIntervalMinutes: Long = 60
) {
    fun getEndpointRateLimits(): Map<String, Double> {
        if (endpoints.isBlank()) return emptyMap()

        return endpoints.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .associate { entry ->
                val parts = entry.split(":")
                val key = parts[0].trim()

                val value = if (parts.size == 2) {
                    parts[1].trim().toDoubleOrNull() ?: default
                } else {
                    default
                }

                Pair(key, value)
            }
    }
}

