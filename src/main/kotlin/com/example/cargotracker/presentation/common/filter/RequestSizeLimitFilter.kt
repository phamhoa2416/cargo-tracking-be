package com.example.cargotracker.presentation.common.filter

import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(3)
class RequestSizeLimitFilter : WebFilter {

    private val maxSize = 10 * 1024 * 1024

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val contentLength = exchange.request.headers.contentLength

        // contentLength is -1 if not specified
        if (contentLength > 0 && contentLength > maxSize) {
            exchange.response.statusCode = HttpStatus.PAYLOAD_TOO_LARGE
            return exchange.response.setComplete()
        }

        return chain.filter(exchange)
    }
}