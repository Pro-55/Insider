package com.example.insider.models

import com.google.gson.JsonObject

data class Lists(
    val masterList: List<Event>?,
    val groups: List<Group>?,
    val categories: List<Category>?
)

data class Group(
    val title: String,
    val items: List<String>
)

data class Category(
    val title: String,
    val items: List<String>
)

fun JsonObject.parseLists(): Lists = Lists(
    masterList = getAsJsonObject("masterList")?.parseEventsMasterList(),
    groups = getAsJsonObject("groupwiseList")?.parseGroups(),
    categories = getAsJsonObject("categorywiseList")?.parseCategories()
)

fun JsonObject.parseGroups(): List<Group>? = keySet()?.mapNotNull { title ->
    val list = getAsJsonArray(title)?.mapNotNull { it.toString() } ?: listOf()
    Group(title, list)
}

fun JsonObject.parseCategories(): List<Category>? = keySet()?.mapNotNull { title ->
    val list = getAsJsonArray(title)?.mapNotNull { it.toString() } ?: listOf()
    Category(title, list)
}