package com.example.insider.models

import com.google.gson.JsonObject

data class Picks(
    val masterList: List<Article>?,
    val groups: List<Group>?,
    val categories: List<Category>?
)

fun JsonObject.parsePicks(): Picks = Picks(
    masterList = getAsJsonObject("masterList")?.parseArticleMasterList(),
    groups = getAsJsonObject("groupwiseList")?.parseGroups(),
    categories = getAsJsonObject("categorywiseList")?.parseCategories()
)