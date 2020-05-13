package com.example.insider.di.home

import androidx.lifecycle.ViewModel
import com.example.insider.di.ViewModelKey
import com.example.insider.ui.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HomeViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

}