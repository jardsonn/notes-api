package com.jalloft.noteskt.utils

import java.util.regex.Pattern

object FieldValidator {

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
        val pattern = Pattern.compile(emailRegex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }
}