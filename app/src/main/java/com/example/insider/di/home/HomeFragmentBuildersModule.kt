package com.example.insider.di.home

import com.example.insider.ui.group.GroupFragment
import com.example.insider.ui.home.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeGroupFragment(): GroupFragment

}