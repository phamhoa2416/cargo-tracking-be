package com.example.cargotracker.infrastructure.security

import com.example.cargotracker.infrastructure.config.RateLimitProperties
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Component
@Order(4)
class RateLimitFilter(
    private val rateLimitProperties: RateLimitProperties
) : WebFilter {
    private val limiters = ConcurrentHashMap<String, RateLimiter>()
    private val endpointRateLimits: Map<String, Double> = rateLimitProperties.getEndpointRateLimits()
    private val cleanupExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "rate-limit-cleanup").apply { isDaemon = true }
    }

    @PostConstruct
    fun init() {
        cleanupExecutor.scheduleWithFixedDelay(
            { cleanupStaleLimiters() },
            rateLimitProperties.cleanupIntervalMinutes,
            rateLimitProperties.cleanupIntervalMinutes,
            TimeUnit.MINUTES
        )
    }

    @PreDestroy
    fun destroy() {
        cleanupExecutor.shutdown()
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow()
            }
        } catch (_: InterruptedException) {
            cleanupExecutor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val clientIp = exchange.request.remoteAddress?.address?.hostAddress ?: "unknown"
        val path = exchange.request.uri.path

        val rateLimit = getRateLimitForPath(path)

        val rateLimiter = limiters.computeIfAbsent(clientIp) {
            RateLimiter.create(rateLimit)
        }

        return if (rateLimiter.allowRequest()) {
            chain.filter(exchange)
        } else {
            exchange.response.statusCode = HttpStatus.TOO_MANY_REQUESTS
            exchange.response.setComplete()
        }
    }

    private fun getRateLimitForPath(path: String): Double {
        endpointRateLimits[path]?.let { return it }

        endpointRateLimits.keys.forEach { endpoint ->
            if (path.startsWith(endpoint)) {
                return endpointRateLimits[endpoint]!!
            }
        }
        
        return rateLimitProperties.default
    }

    private fun cleanupStaleLimiters() {
        val thresholdMinutes = rateLimitProperties.cleanupIntervalMinutes * 2
        val staleKeys = limiters.entries
            .filter { it.value.isStale(thresholdMinutes) }
            .map { it.key }
        
        staleKeys.forEach { key ->
            limiters.remove(key)
        }
    }
}
