package com.example.insider.models

import com.example.insider.util.extensions.deQuoteString
import com.google.gson.JsonObject

data class Filter(
    val key: String,
    val shows: List<Show>
)

data class Show(
    val display: String,
    val key: String,
    var customDate: CustomDate? = null
)

fun JsonObject.parseFilters(): List<Filter>? = keySet()?.mapNotNull {
    Filter(
        key = it,
        shows = getAsJsonObject(it)?.parseShows() ?: listOf()
    )
}

fun JsonObject.parseShows(): List<Show> = getAsJsonArray("show")?.mapNotNull {
    val display = it.asJsonObject.get("display")?.toString()?.deQuoteString() ?: ""
    val key = it.asJsonObject.get("key")?.toString()?.deQuoteString() ?: ""
    Show(display, key)
} ?: listOf()