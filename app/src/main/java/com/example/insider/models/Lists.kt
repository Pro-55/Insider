package com.example.insider.models

import com.example.insider.util.extensions.deQuoteString
import com.google.gson.JsonObject

data class Lists(
    val masterList: List<Event>?,
    val groups: List<Group>?,
    val categories: List<Category>?
)

data class Group(
    val key: String,
    val value: List<String>
)

data class Category(
    val key: String,
    val value: List<String>
)

fun JsonObject.parseLists(): Lists = Lists(
    masterList = getAsJsonObject("masterList")?.parseEventsMasterList(),
    groups = getAsJsonObject("groupwiseList")?.parseGroups(),
    categories = getAsJsonObject("categorywiseList")?.parseCategories()
)

fun JsonObject.parseGroups(): List<Group>? = keySet()?.mapNotNull { title ->
    val list = getAsJsonArray(title)?.mapNotNull { it?.asString?.deQuoteString() } ?: listOf()
    Group(title, list)
}

fun JsonObject.parseCategories(): List<Category>? = keySet()?.mapNotNull { title ->
    val list = getAsJsonArray(title)?.mapNotNull { it?.asString?.deQuoteString() } ?: listOf()
    Category(title, list)
}