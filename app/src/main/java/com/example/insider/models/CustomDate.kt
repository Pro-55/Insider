package com.example.insider.models

import com.example.insider.util.extensions.getAsString
import com.google.gson.JsonObject

data class CustomDate(
    val title: String?,
    val value: String?
)

fun JsonObject.parseDates(): List<CustomDate>? =
    keySet()?.mapNotNull { CustomDate(it, getAsString(it)) }