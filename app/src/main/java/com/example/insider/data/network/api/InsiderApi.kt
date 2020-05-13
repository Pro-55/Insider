package com.example.insider.data.network.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InsiderApi {

    @GET("/home")
    suspend fun fetchData(
        @Query("norm") norm: Int,
        @Query("filterBy") filterBy: String,
        @Query("city") city: String
    ): Response<JsonObject>

}