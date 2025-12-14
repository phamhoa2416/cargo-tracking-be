package com.example.cargotracker.infrastructure.security

import java.time.Instant
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class RateLimiter(
    private val permitsPerSecond: Double,
    val lastAccessTime: AtomicReference<Instant> = AtomicReference(Instant.now())
) {
    private val capacity: Long = permitsPerSecond.toLong()
    private val tokens = AtomicLong(capacity)

    fun allowRequest(): Boolean {
        val now = Instant.now()
        lastAccessTime.set(now)
        
        val lastRefill = lastAccessTime.get()
        val elapsedSeconds = java.time.Duration.between(lastRefill, now).toMillis() / 1000.0

        if (elapsedSeconds > 0) {
            val tokensToAdd = (elapsedSeconds * permitsPerSecond).toLong()
            val currentTokens = tokens.get()
            val newTokens = minOf(capacity, currentTokens + tokensToAdd)
            tokens.set(newTokens)
        }

        val currentTokens = tokens.get()
        return if (currentTokens > 0) {
            tokens.decrementAndGet()
            true
        } else {
            false
        }
    }

    fun isStale(thresholdMinutes: Long): Boolean {
        val threshold = Instant.now().minusSeconds(thresholdMinutes * 60)
        return lastAccessTime.get().isBefore(threshold)
    }

    companion object {
        fun create(permitsPerSecond: Double): RateLimiter {
            return RateLimiter(permitsPerSecond)
        }
    }
}
