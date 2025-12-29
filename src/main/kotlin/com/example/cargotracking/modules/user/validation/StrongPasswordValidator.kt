package com.example.cargotracking.modules.user.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class StrongPasswordValidator : ConstraintValidator<StrongPassword, String> {

    private var minLength: Int = 8
    private var requireUppercase: Boolean = true
    private var requireLowercase: Boolean = true
    private var requireDigit: Boolean = true
    private var requireSpecialChar: Boolean = true

    companion object {
        private val UPPERCASE_PATTERN = Regex("[A-Z]")
        private val LOWERCASE_PATTERN = Regex("[a-z]")
        private val DIGIT_PATTERN = Regex("[0-9]")
        private val SPECIAL_CHAR_PATTERN = Regex("[!@#$%^&*()_+=\\-\\[\\]{}|;':\",./<>?`~]")
    }

    override fun initialize(constraintAnnotation: StrongPassword) {
        minLength = constraintAnnotation.minLength
        requireUppercase = constraintAnnotation.requireUppercase
        requireLowercase = constraintAnnotation.requireLowercase
        requireDigit = constraintAnnotation.requireDigit
        requireSpecialChar = constraintAnnotation.requireSpecialChar
    }

    override fun isValid(password: String?, context: ConstraintValidatorContext?): Boolean {
        if (password.isNullOrBlank()) {
            return false
        }

        val errors = mutableListOf<String>()

        if (password.length < minLength) {
            errors.add("Password must be at least $minLength characters long")
        }

        if (requireUppercase && !UPPERCASE_PATTERN.containsMatchIn(password)) {
            errors.add("Password must contain at least one uppercase letter")
        }

        if (requireLowercase && !LOWERCASE_PATTERN.containsMatchIn(password)) {
            errors.add("Password must contain at least one lowercase letter")
        }

        if (requireDigit && !DIGIT_PATTERN.containsMatchIn(password)) {
            errors.add("Password must contain at least one digit")
        }

        if (requireSpecialChar && !SPECIAL_CHAR_PATTERN.containsMatchIn(password)) {
            errors.add("Password must contain at least one special character")
        }

        if (errors.isNotEmpty()) {
            context?.disableDefaultConstraintViolation()
            context?.buildConstraintViolationWithTemplate(errors.joinToString(". "))
                ?.addConstraintViolation()
            return false
        }

        return true
    }
}

