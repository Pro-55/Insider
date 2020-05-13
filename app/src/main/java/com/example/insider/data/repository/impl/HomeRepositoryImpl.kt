package com.example.insider.data.repository.impl

import com.example.insider.data.network.api.InsiderApi
import com.example.insider.data.repository.contract.HomeRepository
import com.example.insider.models.Data
import com.example.insider.models.Resource
import com.example.insider.models.parseData
import com.example.insider.util.Constants
import com.example.insider.util.extensions.resourceFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: InsiderApi
) : HomeRepository {

    companion object {
        private val TAG = HomeRepositoryImpl::class.java.simpleName
    }


    override fun fetchData(norm: Int, filterBy: String, city: String): Flow<Resource<Data>> {
        return resourceFlow {

            val result = api.fetchData(norm, filterBy, city)

            if (result.isSuccessful) {
                val data = result.body()
                if (data != null) emit(Resource.success(data.parseData()))
                else emit(Resource.error(Constants.REQUEST_FAILED_MESSAGE))
            } else {
                val msg = result.message()
                emit(Resource.error(msg))
            }
        }
    }

}