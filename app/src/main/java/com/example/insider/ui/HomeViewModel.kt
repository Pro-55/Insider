package com.example.insider.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insider.data.repository.impl.HomeRepositoryImpl
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val repository: HomeRepositoryImpl
) : ViewModel() {

    companion object {
        private val TAG = HomeViewModel::class.java.simpleName
    }

    init {
        fetchData()
    }

    private fun fetchData() {
        repository.fetchData(norm = 1, filterBy = "go-out", city = "mumbai")
            .onEach { Log.d(TAG, "TestLog: d:${it.data}") }
            .launchIn(viewModelScope)
    }

}