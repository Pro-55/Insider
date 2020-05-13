package com.example.insider.models

import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class Sorters(
    val digitalEvents: List<Sort>,
    val events: List<Sort>,
    val food: List<Sort>,
    val travel: List<Sort>,
    val workshops: List<Sort>,
    val sports: List<Sort>,
    val theatre: List<Sort>
)

data class Sort(
    val display: String,
    val key: String,
    val type: String
)

fun JsonObject.parseSorters(): Sorters = Sorters(
    digitalEvents = getAsJsonArray("Digital Events")?.parseSorts() ?: listOf(),
    events = getAsJsonArray("Events")?.parseSorts() ?: listOf(),
    food = getAsJsonArray("Food")?.parseSorts() ?: listOf(),
    travel = getAsJsonArray("Travel")?.parseSorts() ?: listOf(),
    workshops = getAsJsonArray("Workshops")?.parseSorts() ?: listOf(),
    sports = getAsJsonArray("Sports")?.parseSorts() ?: listOf(),
    theatre = getAsJsonArray("Theatre")?.parseSorts() ?: listOf()
)

fun JsonArray.parseSorts(): List<Sort> = mapNotNull {
    val display = it.asJsonObject?.get("display")?.toString() ?: ""
    val key = it.asJsonObject?.get("key")?.toString() ?: ""
    val type = it.asJsonObject?.get("type")?.toString() ?: ""
    Sort(display, key, type)
}