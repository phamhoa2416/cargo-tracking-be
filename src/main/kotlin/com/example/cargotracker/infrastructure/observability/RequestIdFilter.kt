package com.example.cargotracker.infrastructure.observability

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

@Component
@Order(1)
class RequestIdFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestId = exchange.request.headers["X-Request-ID"]?.firstOrNull()
            ?: UUID.randomUUID().toString()

        exchange.response.headers.add("X-Request-ID", requestId)
        exchange.attributes["requestId"] = requestId

        return chain.filter(exchange)
    }
}