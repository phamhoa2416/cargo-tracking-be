package com.example.cargotracker.presentation.common.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.full.memberProperties

class PasswordNotMatchValidator : ConstraintValidator<PasswordNotMatch, Any> {
    private lateinit var oldPasswordFieldName: String
    private lateinit var newPasswordFieldName: String

    override fun initialize(constraintAnnotation: PasswordNotMatch) {
        oldPasswordFieldName = constraintAnnotation.oldPasswordField
        newPasswordFieldName = constraintAnnotation.newPasswordField
    }

    override fun isValid(value: Any?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        return try {
            val oldPassword = getFieldValue(value, oldPasswordFieldName) as? String
            val newPassword = getFieldValue(value, newPasswordFieldName) as? String

            if (oldPassword == null || newPassword == null) {
                return true
            }

            oldPassword != newPassword
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

