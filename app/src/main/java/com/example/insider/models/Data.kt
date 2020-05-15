package com.example.insider.models

import com.example.insider.util.extensions.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class Data(
    val tags: List<String>,
    val groups: List<String>,
    val filters: List<Filter>,
    val sorters: List<Sorter>,
    val lists: Lists?,
    val picks: Picks?,
    val populars: List<Event>,
    val text: List<Text>,
    val featured: List<Event>,
    val dates: List<CustomDate>,
    val banners: List<Banner>
) {

    fun getBannersHome(): List<Banner> =
        banners.filter { b -> b.group?.name?.contains("home", true) == true }

    fun getBannersFor(key: String): List<Banner> = banners.filter { b -> b.group?.name == key }

    fun getShowsFor(key: String): List<Show>? = filters.find { f -> f.key == key }?.shows
        ?.map { s ->
            val x = dates.find { d -> d.title == "${s.key}_date_string" }
            s.copy(customDate = x)
        }

    fun getSortsFor(key: String): List<Sort>? = sorters.find { s -> s.key == key }?.sorts
}

fun JsonObject.parseData() = Data(
    tags = getAsJsonArray("tags")?.parseTags() ?: listOf(),
    groups = getAsJsonArray("groups")?.parseGroups() ?: listOf(),
    filters = getAsJsonObject("filters")?.parseFilters() ?: listOf(),
    sorters = getAsJsonObject("sorters")?.parseSorters() ?: listOf(),
    lists = getAsJsonObject("list")?.parseLists(),
    picks = getAsJsonObject("picks")?.parsePicks(),
    populars = getAsJsonArray("popular")?.parsePopulars() ?: listOf(),
    text = getAsJsonObject("text")?.parseTexts() ?: listOf(),
    featured = getAsJsonArray("featured")?.parseFeatured() ?: listOf(),
    dates = getAsJsonObject("dates")?.parseDates() ?: listOf(),
    banners = getAsJsonArray("banners")?.parseBanners() ?: listOf()
)

fun JsonArray.parseTags() = mapNotNull { it?.asString }

fun JsonArray.parseGroups() = mapNotNull { it?.asString }

fun JsonArray.parseFeatured() = mapNotNull {
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