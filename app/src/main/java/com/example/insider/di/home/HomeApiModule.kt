package com.example.insider.di.home

import com.example.insider.data.network.api.InsiderApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object HomeApiModule {

    @HomeScope
    @Provides
    fun provideInsiderApi(retrofit: Retrofit): InsiderApi {
        return retrofit.create(InsiderApi::class.java)
    }

}