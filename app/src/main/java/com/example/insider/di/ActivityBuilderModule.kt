package com.example.insider.di

import com.example.insider.di.home.*
import com.example.insider.ui.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @HomeScope
    @ContributesAndroidInjector(
        modules = [
            HomeViewModelsModule::class,
            HomeFragmentBuildersModule::class,
            HomeRepositoryModule::class,
            HomeApiModule::class
        ]
    )
    abstract fun contributeHomeActivity(): HomeActivity

}