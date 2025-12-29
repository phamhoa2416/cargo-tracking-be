package com.example.cargotracking.modules.user.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [StrongPasswordValidator::class])
@MustBeDocumented
annotation class StrongPassword(
    val message: String = "Password must be at least 8 characters and contain at least one uppercase letter, one lowercase letter, one digit, and one special character",
    val minLength: Int = 8,
    val requireUppercase: Boolean = true,
    val requireLowercase: Boolean = true,
    val requireDigit: Boolean = true,
    val requireSpecialChar: Boolean = true,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

