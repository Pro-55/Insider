package com.example.insider.di.home

import com.example.insider.data.network.api.InsiderApi
import com.example.insider.data.repository.impl.HomeRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
object HomeRepositoryModule {

    @HomeScope
    @Provides
    fun provideHomeRepository(api: InsiderApi) = HomeRepositoryImpl(api)

}