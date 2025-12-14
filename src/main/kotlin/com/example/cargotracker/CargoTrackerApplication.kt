package com.example.cargotracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import com.example.cargotracker.infrastructure.config.JwtProperties
import com.example.cargotracker.infrastructure.config.RateLimitProperties

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class, RateLimitProperties::class)
class CargoTrackerApplication

fun main(args: Array<String>) {
	runApplication<CargoTrackerApplication>(*args)
}
