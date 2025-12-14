package com.example.cargotracker.presentation.common.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.full.memberProperties

class PasswordMatchValidator : ConstraintValidator<PasswordMatch, Any> {
    private lateinit var passwordFieldName: String
    private lateinit var confirmPasswordFieldName: String

    override fun initialize(constraintAnnotation: PasswordMatch) {
        passwordFieldName = constraintAnnotation.passwordField
        confirmPasswordFieldName = constraintAnnotation.confirmPasswordField
    }

    override fun isValid(value: Any?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        return try {
            val password = getFieldValue(value, passwordFieldName) as? String
            val confirmPassword = getFieldValue(value, confirmPasswordFieldName) as? String

            if (password == null || confirmPassword == null) {
                return true
            }

            password == confirmPassword
        } catch (e: Exception) {
            false
        }
    }

    private fun getFieldValue(obj: Any, fieldName: String): Any? {
        val kClass = obj::class
        val property = kClass.memberProperties.find { it.name == fieldName }
        return property?.call(obj)
    }
}

