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
    val venueId: String?,
    val venueName: String?,
    val venueDate: String?,
    val venueLocation: Location?,
    val isFavorite: Boolean = false,
    val category: SubData?,
    val group: SubData?,
    val price: String?,
    val applicableFilters: List<String>?,
    val popularityScore: Float?,
    val minPrice: Int?
) {
    fun getDisplayPrice(): String {
        price ?: return ""
        return if (price.contains("free", true)) price else "${Constants.RUPEE_SYMBOL}$price"
    }

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        other as Event?

        if (_id != other._id) return false
        if (isFavorite != other.isFavorite) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _id?.hashCode() ?: 0
        result = 31 * result + (startTime?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (slug?.hashCode() ?: 0)
        result = 31 * result + (horizontalCover?.hashCode() ?: 0)
        result = 31 * result + (verticalCover?.hashCode() ?: 0)
        result = 31 * result + (venueId?.hashCode() ?: 0)
        result = 31 * result + (venueName?.hashCode() ?: 0)
        result = 31 * result + (venueDate?.hashCode() ?: 0)
        result = 31 * result + (venueLocation?.hashCode() ?: 0)
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + (category?.hashCode() ?: 0)
        result = 31 * result + (group?.hashCode() ?: 0)
        result = 31 * result + (price?.hashCode() ?: 0)
        result = 31 * result + (applicableFilters?.hashCode() ?: 0)
        result = 31 * result + (popularityScore?.hashCode() ?: 0)
        result = 31 * result + (minPrice ?: 0)
        return result
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
    val count: Int = 0
) {
    fun getEventCount(): String = if (count == 1) "$count event" else "$count events"
}

fun JsonObject.parseEventsMasterList(): List<Event>? = keySet()?.mapNotNull {
    val event = getAsJsonObject(it)
    Event(
        _id = event?.getAsString("_id"),
        startTime = event?.getAsLong("min_show_start_time"),
        name = event?.getAsString("name"),
        slug = event?.getAsString("slug"),
        horizontalCover = event?.getAsString("horizontal_cover_image"),
        verticalCover = event?.getAsString("vertical_cover_image"),
        venueId = event?.getAsString("venue_id"),
        venueName = event?.getAsString("venue_name"),
        venueDate = event?.getAsString("venue_date_string"),
        venueLocation = event?.getAsJsonObject("venue_geolocation")?.parseLocation(),
        category = event?.getAsJsonObject("category_id")?.parseSubData(),
        group = event?.getAsJsonObject("group_id")?.parseSubData(),
        price = event?.getAsString("price_display_string"),
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
        venueId = event?.getAsString("venue_id"),
        venueName = event?.getAsString("venue_name"),
        venueDate = event?.getAsString("venue_date_string"),
        venueLocation = event?.getAsJsonObject("venue_geolocation")?.parseLocation(),
        category = event?.getAsJsonObject("category_id")?.parseSubData(),
        group = event?.getAsJsonObject("group_id")?.parseSubData(),
        price = event?.getAsString("price_display_string"),
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
        venueId = event?.getAsString("venue_id"),
        venueName = event?.getAsString("venue_name"),
        venueDate = event?.getAsString("venue_date_string"),
        venueLocation = event?.getAsJsonObject("venue_geolocation")?.parseLocation(),
        category = event?.getAsJsonObject("category_id")?.parseSubData(),
        group = event?.getAsJsonObject("group_id")?.parseSubData(),
        price = event?.getAsString("price_display_string"),
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
    icon = getAsString("icon_img")
)

fun JsonArray.parseApplicableFilters(): List<String> = mapNotNull { it?.asString }