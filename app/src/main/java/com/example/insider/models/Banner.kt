package com.example.insider.models

import com.example.insider.util.extensions.getAsBoolean
import com.example.insider.util.extensions.getAsInt
import com.example.insider.util.extensions.getAsString
import com.google.gson.JsonArray

data class Banner(
    val _id: String?,
    val isInternal: Boolean?,
    val name: String?,
    val type: String?,
    val category: SubData?,
    val group: SubData?,
    val mapLink: String?,
    val verticalCover: String?,
    val priority: Int?,
    val details: DisplayDetails?
)

fun JsonArray.parseBanners(): List<Banner> = mapNotNull {
    val banner = it?.asJsonObject
    Banner(
        _id = banner?.getAsString("_id"),
        isInternal = banner?.getAsBoolean("is_internal"),
        name = banner?.getAsString("name"),
        type = banner?.getAsString("type"),
        category = banner?.getAsJsonObject("category_id")?.parseSubData(),
        group = banner?.getAsJsonObject("group_id")?.parseSubData(),
        mapLink = banner?.getAsString("map_link"),
        verticalCover = banner?.getAsString("vertical_cover_image"),
        priority = banner?.getAsInt("priority"),
        details = banner?.getAsJsonObject("display_details")?.parseDisplayDetails()
    )
}