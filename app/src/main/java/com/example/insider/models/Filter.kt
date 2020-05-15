package com.example.insider.models

import com.example.insider.util.extensions.deQuoteString
import com.example.insider.util.extensions.getAsString
import com.google.gson.JsonObject

data class Filter(
    val key: String,
    val shows: List<Show>?
)

data class Show(
    val display: String?,
    val key: String?,
    var customDate: CustomDate? = null
)

data class CustomDate(
    val title: String?,
    val value: String?
)

fun JsonObject.parseFilters(): List<Filter>? =
    keySet()?.mapNotNull { Filter(it, getAsJsonObject(it)?.parseShows()) }

fun JsonObject.parseShows(): List<Show>? = getAsJsonArray("show")?.mapNotNull {
    val display = it.asJsonObject.get("display")?.toString()?.deQuoteString()
    val key = it.asJsonObject.get("key")?.toString()?.deQuoteString()
    Show(display, key)
}

fun JsonObject.parseDates(): List<CustomDate>? =
    keySet()?.mapNotNull { CustomDate(it, getAsString(it)) }