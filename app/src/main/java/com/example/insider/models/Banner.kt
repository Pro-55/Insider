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
    val priority: Int?
) {
    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        other as Banner?

        if (_id != other._id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _id?.hashCode() ?: 0
        result = 31 * result + (isInternal?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (category?.hashCode() ?: 0)
        result = 31 * result + (group?.hashCode() ?: 0)
        result = 31 * result + (mapLink?.hashCode() ?: 0)
        result = 31 * result + (verticalCover?.hashCode() ?: 0)
        result = 31 * result + (priority ?: 0)
        return result
    }

}

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
        priority = banner?.getAsInt("priority")
    )
}