package com.jalloft.noteskt.utils

import java.util.*

fun String.toUUID(): UUID? =
    try {
        UUID.fromString(this)
    } catch (_: Throwable) {
        null
    }