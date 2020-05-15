package com.example.insider.models

import com.example.insider.util.Constants
import com.example.insider.util.extensions.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class Event(
    val _id: String?,
    val startTime: Long?,
    val name: String?,
    val slug: String?,
    val horizontalCover: String?,
    val verticalCover: String?,
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
    val applicableFilters: List<String>?,
    val popularityScore: Float?,
    val minPrice: Int?
) {
    fun getDisplayPrice(): String {
        price ?: return ""
        return if (price.contains("free", true)) price else "${Constants.RUPEE_SYMBOL}$price"
    }
}

data class Location(
    val latitude: Double,
    val longitude: Double
)

data class SubData(
    val _id: String?,
    val name: String?,
    val icon: String?,
    val slug: String?
)

fun JsonObject.parseEventsMasterList(): List<Event>? = keySet()?.mapNotNull {
    val event = getAsJsonObject(it)
    Event(
        _id = event?.getAsString("_id"),
        startTime = event?.getAsLong("min_show_start_time"),
        name = event?.getAsString("name"),
        slug = event?.getAsString("slug"),
        horizontalCover = event?.getAsString("horizontal_cover_image"),
        verticalCover = event?.getAsString("vertical_cover_image"),
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
        applicableFilters = event?.getAsJsonArray("applicable_filters")?.parseApplicableFilters(),
        popularityScore = event?.getAsFloat("popularity_score"),
        minPrice = event?.getAsInt("min_price")
    )
}

fun JsonArray.parsePopulars(): List<Event> = mapNotNull {
    val event = it?.asJsonObject
    Event(
        _id = event?.getAsString("_id"),
        startTime = event?.getAsLong("min_show_start_time"),
        name = event?.getAsString("name"),
        slug = event?.getAsString("slug"),
        horizontalCover = event?.getAsString("horizontal_cover_image"),
        verticalCover = event?.getAsString("vertical_cover_image"),
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
        applicableFilters = event?.getAsJsonArray("applicable_filters")?.parseApplicableFilters(),
        popularityScore = event?.getAsFloat("popularity_score"),
        minPrice = event?.getAsInt("min_price")
    )
}

fun JsonArray.parseFeatured(): List<Event> = mapNotNull {
    val event = it?.asJsonObject
    Event(
        _id = event?.getAsString("_id"),
        startTime = event?.getAsLong("min_show_start_time"),
        name = event?.getAsString("name"),
        slug = event?.getAsString("slug"),
        horizontalCover = event?.getAsString("horizontal_cover_image"),
        verticalCover = event?.getAsString("vertical_cover_image"),
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
        applicableFilters = event?.getAsJsonArray("applicable_filters")?.parseApplicableFilters(),
        popularityScore = event?.getAsFloat("popularity_score"),
        minPrice = event?.getAsInt("min_price")
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
    slug = getAsString("slug")
)

fun JsonArray.parseApplicableFilters(): List<String> = mapNotNull { it?.asString }