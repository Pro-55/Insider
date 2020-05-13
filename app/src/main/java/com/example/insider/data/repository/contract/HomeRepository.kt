package com.example.insider.data.repository.contract

import com.example.insider.models.Data
import com.example.insider.models.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun fetchData(norm: Int, filterBy: String, city: String): Flow<Resource<Data>>

}