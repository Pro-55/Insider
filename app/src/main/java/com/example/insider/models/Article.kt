package com.example.insider.models

import com.example.insider.util.extensions.getAsBoolean
import com.example.insider.util.extensions.getAsInt
import com.example.insider.util.extensions.getAsString
import com.google.gson.JsonObject

data class Article(
    val _id: String?,
    val tags: List<Tag>?,
    val slug: String?,
    val title: String?,
    val summary: String?,
    val headerImage: String?,
    val category: SubData?,
    val group: SubData?,
    val headerType: String?,
    val createdBy: String?,
    val timeModified: String?,
    val timeAdded: String?,
    val _v: Int?,
    val publishDate: String?,
    val model: String?,
    val horizontalCover: String?,
    val propertyTag: Tag?,
    val orderIndex: Int?
)

fun JsonObject.parseArticleMasterList(): List<Article>? = keySet()?.mapNotNull {
    val article = getAsJsonObject(it)
    Article(
        _id = article?.getAsString("_id"),
        tags = article?.getAsJsonArray("tags")?.parseEventTags(),
        slug = article?.getAsString("slug"),
        title = article?.getAsString("title"),
        summary = article?.getAsString("summary"),
        headerImage = article?.getAsString("headerImage"),
        category = article?.getAsJsonObject("category_id")?.parseSubData(),
        group = article?.getAsJsonObject("group_id")?.parseSubData(),
        headerType = article?.getAsString("header_type"),
        createdBy = article?.getAsString("created_by"),
        timeModified = article?.getAsString("timestamp_modified"),
        timeAdded = article?.getAsString("timestamp_added"),
        _v = article?.getAsInt("__v"),
        publishDate = article?.getAsString("publish_date"),
        model = article?.getAsString("model"),
        horizontalCover = article?.getAsString("horizontal_cover_image"),
        propertyTag = article?.getAsJsonObject("propertyTag")?.parsePropertyTag(),
        orderIndex = article?.getAsInt("orderIndex")
    )
}

fun JsonObject.parsePropertyTag(): Tag = Tag(
    _id = getAsString("_id"),
    tagId = getAsString("tag_id"),
    priority = getAsInt("priority"),
    isFeatured = getAsBoolean("is_featured"),
    isCarousel = getAsBoolean("is_carousel"),
    isPick = getAsBoolean("is_pick"),
    isPrimaryInterest = getAsBoolean("is_primary_interest")
)