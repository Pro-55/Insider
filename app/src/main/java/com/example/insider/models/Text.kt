package com.example.insider.models

import com.example.insider.util.extensions.getAsString
import com.google.gson.JsonObject

data class Text(
    val title: String?,
    val data: TextData?
)

data class TextData(
    val allTitle: String?,
    val allDescription: String?,
    val pickTitle: String?,
    val pickDescription: String?,
    val colour: String?
)

fun JsonObject.parseTexts(): List<Text>? =
    keySet()?.mapNotNull { Text(it, getAsJsonObject(it)?.parseTextData()) }

fun JsonObject.parseTextData(): TextData = TextData(
    allTitle = getAsString("all_title"),
    allDescription = getAsString("all_description"),
    pickTitle = getAsString("pick_title"),
    pickDescription = getAsString("pick_description"),
    colour = getAsString("colour")
)