package com.example.insider.models

import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class Data(
    val groups: List<String>?,
    val filters: List<Filter>?,
    val sorters: List<Sorter>?,
    val lists: Lists?,
    val populars: List<Event>?,
    val featured: List<Event>?,
    val dates: List<CustomDate>?,
    val banners: List<Banner>?
) {

    fun getBannersHome(): List<Banner>? =
        banners?.mapNotNull { b -> if (b.group?.name?.contains("home", true) == true) b else null }

    fun getBannersFor(key: String): List<Banner>? =
        banners?.mapNotNull { b -> if (b.group?.name == key) b else null }

    fun getShowsFor(key: String): List<Show>? = filters?.find { f -> f.key == key }?.shows
        ?.map { s ->
            val x = dates?.find { d -> d.title == "${s.key}_date_string" }
            s.copy(customDate = x)
        }

    fun getSortsFor(key: String): List<Sort>? = sorters?.find { s -> s.key == key }?.sorts

    fun getGroupedListFor(key: String): List<Event>? {
        val k = lists?.groups?.find { g -> g.key == key }?.value
        return lists?.masterList?.mapNotNull { e -> if (k?.contains(e.slug) == true) e else null }
    }

    fun getCategorisedListFor(key: String): List<Event>? {
        val k = lists?.categories?.find { c -> c.key == key }?.value
        return lists?.masterList?.mapNotNull { e -> if (k?.contains(e.slug) == true) e else null }
    }

    fun getPopularsList(): List<Event>? = populars?.sortedBy { e -> e.popularityScore }

    fun getCategoriesShortList(): List<SubData>? {
        val categories = getCategories()
        if (categories.isNullOrEmpty()) return null
        return if (categories.size >= 8) categories.subList(0, 8) else categories
    }

    fun getCategories(): List<SubData>? = lists?.masterList?.groupBy { e -> e.category }
        ?.mapNotNull { m -> m.key?.copy(count = m.value.size) }
        ?.sortedByDescending { c -> c.count }

}

fun JsonObject.parseData(): Data = Data(
    groups = getAsJsonArray("groups")?.parseGroups(),
    filters = getAsJsonObject("filters")?.parseFilters(),
    sorters = getAsJsonObject("sorters")?.parseSorters(),
    lists = getAsJsonObject("list")?.parseLists(),
    populars = getAsJsonArray("popular")?.parsePopulars(),
    featured = getAsJsonArray("featured")?.parseFeatured(),
    dates = getAsJsonObject("dates")?.parseDates(),
    banners = getAsJsonArray("banners")?.parseBanners()
)

fun JsonArray.parseGroups(): List<String> = mapNotNull { it?.asString }