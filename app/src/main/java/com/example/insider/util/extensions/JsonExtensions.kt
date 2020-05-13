package com.example.insider.util.extensions

import com.google.gson.JsonObject

fun JsonObject.getAsString(member: String): String? = get(member)?.asString

fun JsonObject.getAsInt(member: String): Int? = get(member)?.asInt

fun JsonObject.getAsLong(member: String): Long? = get(member)?.asLong

fun JsonObject.getAsFloat(member: String): Float? = get(member)?.asFloat

fun JsonObject.getAsDouble(member: String): Double? = get(member)?.asDouble

fun JsonObject.getAsBoolean(member: String): Boolean? = get(member)?.asBoolean