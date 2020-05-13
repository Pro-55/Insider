package com.example.insider.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.insider.util.Constants.INSIDER_SHARED_PREFS
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences(INSIDER_SHARED_PREFS, Context.MODE_PRIVATE)

}