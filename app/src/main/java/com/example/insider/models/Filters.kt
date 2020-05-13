package com.example.insider.models

import com.google.gson.JsonObject

data class Filters(
    val digitalEvents: List<Show>,
    val events: List<Show>,
    val food: List<Show>,
    val travel: List<Show>,
    val workshops: List<Show>,
    val sports: List<Show>,
    val theatre: List<Show>
)

data class Show(
    val display: String,
    val key: String
)

fun JsonObject.parseFilters(): Filters = Filters(
    digitalEvents = getAsJsonObject("Digital Events")?.parseShows() ?: listOf(),
    events = getAsJsonObject("Events")?.parseShows() ?: listOf(),
    food = getAsJsonObject("Food")?.parseShows() ?: listOf(),
    travel = getAsJsonObject("Travel")?.parseShows() ?: listOf(),
    workshops = getAsJsonObject("Workshops")?.parseShows() ?: listOf(),
    sports = getAsJsonObject("Sports")?.parseShows() ?: listOf(),
    theatre = getAsJsonObject("Theatre")?.parseShows() ?: listOf()
)

fun JsonObject.parseShows(): List<Show> = getAsJsonArray("show")?.mapNotNull {
    val display = it.asJsonObject.get("display")?.toString() ?: ""
    val key = it.asJsonObject.get("key")?.toString() ?: ""
    Show(display, key)
} ?: listOf()