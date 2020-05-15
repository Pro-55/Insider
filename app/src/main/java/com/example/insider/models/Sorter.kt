package com.example.insider.models

import com.example.insider.util.extensions.deQuoteString
import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class Sorter(
    val key: String,
    val sorts: List<Sort>?
)

data class Sort(
    val display: String?,
    val key: String?,
    val type: String?
)

fun JsonObject.parseSorters(): List<Sorter>? =
    keySet()?.mapNotNull { Sorter(it, getAsJsonArray(it)?.parseSorts()) }

fun JsonArray.parseSorts(): List<Sort> = mapNotNull {
    val sort = it.asJsonObject
    Sort(
        display = sort?.get("display")?.toString()?.deQuoteString(),
        key = sort?.get("key")?.toString()?.deQuoteString(),
        type = sort?.get("type")?.toString()?.deQuoteString()
    )
}