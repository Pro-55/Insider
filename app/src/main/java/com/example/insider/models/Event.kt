package com.example.insider.models

import com.example.insider.util.extensions.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class Event(
    val _id: String?,
    val startTime: Long?,
    val name: String?,
    val type: String?,
    val slug: String?,
    val horizontalCover: String?,
    val tags: List<Tag>?,
    val city: String?,
    val venueId: String?,
    val venueName: String?,
    val venueDate: String?,
    val venueLocation: Location?,
    val isRsvp: Boolean?,
    val category: SubData?,
    val group: SubData?,
    val eventState: String?,
    val price: String?,
    val communicationStrategy: String?,
    val model: String?,
    val applicableFilters: List<String>?,
    val popularityScore: Float?,
    val favStats: Stats?,
    val purchaseVisibility: String?,
    val minPrice: Int?
)

data class Tag(
    val _id: String?,
    val tagId: String?,
    val priority: Int?,
    val isFeatured: Boolean?,
    val isCarousel: Boolean?,
    val isPick: Boolean?,
    val isPrimaryInterest: Boolean?
)

data class Location(
    val latitude: Double,
    val longitude: Double
)

data class SubData(
    val _id: String?,
    val name: String?,
    val icon: String?,
    val details: DisplayDetails?,
    val slug: String?
)

data class DisplayDetails(
    val title: String?,
    val description: String?
)

data class Stats(
    val _id: String?,
    val actualCount: Int?,
    val prettyCount: Int?
)

fun JsonObject.parseEventsMasterList(): List<Event>? = keySet()?.mapNotNull {
    val event = getAsJsonObject(it)
    Event(
        _id = event?.getAsString("_id"),
        startTime = event?.getAsLong("min_show_start_time"),
        name = event?.getAsString("name"),
        type = event?.getAsString("type"),
        slug = event?.getAsString("slug"),
        horizontalCover = event?.getAsString("horizontal_cover_image"),
        tags = event?.getAsJsonArray("tags")?.parseEventTags(),
        city = event?.getAsString("city"),
        venueId = event?.getAsString("venue_id"),
        venueName = event?.getAsString("venue_name"),
        venueDate = event?.getAsString("venue_date_string"),
        venueLocation = event?.getAsJsonObject("venue_geolocation")?.parseLocation(),
        isRsvp = event?.getAsBoolean("is_rsvp"),
        category = event?.getAsJsonObject("category_id")?.parseSubData(),
        group = event?.getAsJsonObject("group_id")?.parseSubData(),
        eventState = event?.getAsString("event_state"),
        price = event?.getAsString("price_display_string"),
        communicationStrategy = event?.getAsString("communication_strategy"),
        model = event?.getAsString("model"),
        applicableFilters = event?.getAsJsonArray("applicable_filters")?.parseApplicableFilters(),
        popularityScore = event?.getAsFloat("popularity_score"),
        favStats = event?.getAsJsonObject("favStats")?.parseStats(),
        purchaseVisibility = event?.getAsString("purchase_visibility"),
        minPrice = event?.getAsInt("min_price")
    )
}

fun JsonArray.parseEventTags(): List<Tag> = mapNotNull {
    val tag = it?.asJsonObject
    Tag(
        _id = tag?.getAsString("_id"),
        tagId = tag?.getAsString("tag_id"),
        priority = tag?.getAsInt("priority"),
        isFeatured = tag?.getAsBoolean("is_featured"),
        isCarousel = tag?.getAsBoolean("is_carousel"),
        isPick = tag?.getAsBoolean("is_pick"),
        isPrimaryInterest = tag?.getAsBoolean("is_primary_interest")
    )
}

fun JsonObject.parseLocation(): Location = Location(
    latitude = getAsDouble("latitude") ?: 0.0,
    longitude = getAsDouble("longitude") ?: 0.0
)

fun JsonObject.parseSubData(): SubData = SubData(
    _id = getAsString("_id"),
    name = getAsString("name"),
    icon = getAsString("icon_img"),
    details = getAsJsonObject("display_details")?.parseDisplayDetails(),
    slug = getAsString("slug")
)

fun JsonObject.parseDisplayDetails(): DisplayDetails = DisplayDetails(
    title = getAsString("seo_title"),
    description = getAsString("seo_description")
)

fun JsonArray.parseApplicableFilters(): List<String> = mapNotNull { it?.asString }

fun JsonObject.parseStats(): Stats = Stats(
    _id = getAsString("target_id"),
    actualCount = getAsInt("actualCount"),
    prettyCount = getAsInt("prettyCount")
)

fun JsonArray.parsePopulars(): List<Event> = mapNotNull {
    val event = it?.asJsonObject
    Event(
        _id = event?.getAsString("_id"),
        startTime = event?.getAsLong("min_show_start_time"),
        name = event?.getAsString("name"),
        type = event?.getAsString("type"),
        slug = event?.getAsString("slug"),
        horizontalCover = event?.getAsString("horizontal_cover_image"),
        tags = event?.getAsJsonArray("tags")?.parseEventTags(),
        city = event?.getAsString("city"),
        venueId = event?.getAsString("venue_id"),
        venueName = event?.getAsString("venue_name"),
        venueDate = event?.getAsString("venue_date_string"),
        venueLocation = event?.getAsJsonObject("venue_geolocation")?.parseLocation(),
        isRsvp = event?.getAsBoolean("is_rsvp"),
        category = event?.getAsJsonObject("category_id")?.parseSubData(),
        group = event?.getAsJsonObject("group_id")?.parseSubData(),
        eventState = event?.getAsString("event_state"),
        price = event?.getAsString("price_display_string"),
        communicationStrategy = event?.getAsString("communication_strategy"),
        model = event?.getAsString("model"),
        applicableFilters = event?.getAsJsonArray("applicable_filters")?.parseApplicableFilters(),
        popularityScore = event?.getAsFloat("popularity_score"),
        favStats = event?.getAsJsonObject("favStats")?.parseStats(),
        purchaseVisibility = event?.getAsString("purchase_visibility"),
        minPrice = event?.getAsInt("min_price")
    )
}