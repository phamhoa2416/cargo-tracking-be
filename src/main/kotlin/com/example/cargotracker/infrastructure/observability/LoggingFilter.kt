package com.example.cargotracker.infrastructure.observability

import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(2)
class LoggingFilter : WebFilter {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val start = System.currentTimeMillis()
        val request = exchange.request

        return chain.filter(exchange)
            .doFinally {
                val duration = System.currentTimeMillis() - start
                val statusCode = exchange.response.statusCode?.value() ?: 0
                logger.info(
                    "method={} path={} status={} latency={}ms",
                    request.method,
                    request.uri.path,
                    statusCode,
                    duration
                )
            }
    }
}