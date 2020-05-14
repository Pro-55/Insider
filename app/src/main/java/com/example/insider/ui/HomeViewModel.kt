package com.example.insider.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insider.data.repository.impl.HomeRepositoryImpl
import com.example.insider.models.Data
import com.example.insider.models.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val repository: HomeRepositoryImpl
) : ViewModel() {

    companion object {
        private val TAG = HomeViewModel::class.java.simpleName
    }

    private val _data = MutableLiveData<Resource<Data>>()
    val data: LiveData<Resource<Data>> = _data

    fun fetchData() {
        repository.fetchData(norm = 1, filterBy = "go-out", city = "mumbai")
            .onEach { _data.postValue(it) }
            .launchIn(viewModelScope)
    }

}