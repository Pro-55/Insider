package com.example.insider.models

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
        banners.mapNotNull { b -> if (b.group?.name?.contains("home", true) == true) b else null }

    fun getBannersFor(key: String): List<Banner> =
        banners.mapNotNull { b -> if (b.group?.name == key) b else null }

    fun getShowsFor(key: String): List<Show>? = filters.find { f -> f.key == key }?.shows
        ?.map { s ->
            val x = dates.find { d -> d.title == "${s.key}_date_string" }
            s.copy(customDate = x)
        }

    fun getSortsFor(key: String): List<Sort>? = sorters.find { s -> s.key == key }?.sorts

    fun getGroupedListFor(key: String): List<Event>? {
        val k = lists?.groups?.find { g -> g.key == key }?.value
        return lists?.masterList?.mapNotNull { e -> if (k?.contains(e.slug) == true) e else null }
    }

    fun getPopularsList(): List<Event> = populars.sortedBy { e -> e.popularityScore }

}

fun JsonObject.parseData(): Data = Data(
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

fun JsonArray.parseTags(): List<String> = mapNotNull { it?.asString }

fun JsonArray.parseGroups(): List<String> = mapNotNull { it?.asString }