package com.example.cargotracker.infrastructure.security

import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(5)
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // Skip authentication for public endpoints
        val path = exchange.request.uri.path
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange)
        }

        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            ?: return unauthorized(exchange)

        if (!authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange)
        }

        // Extract token from "Bearer <token>"
        val token = authHeader.substring(7)

        return jwtProvider.validate(token)
            .flatMap { claims ->
                exchange.attributes["userId"] = claims.userId
                exchange.attributes["email"] = claims.email
                exchange.attributes["role"] = claims.role
                chain.filter(exchange)
            }
            .onErrorResume {
                unauthorized(exchange)
            }
    }

    private fun isPublicEndpoint(path: String): Boolean {
        val publicPaths = listOf(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/forgot-password",
            "/api/auth/reset-password"
        )
        return publicPaths.any { path.startsWith(it) }
    }

    private fun unauthorized(exchange: ServerWebExchange): Mono<Void> {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }
}