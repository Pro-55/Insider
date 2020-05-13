package com.example.insider.di.home

import com.example.insider.ui.home.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

}