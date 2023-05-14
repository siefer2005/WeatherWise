package com.juanantbuit.weatherproject.data.datasources.openweather

import com.juanantbuit.weatherproject.data.datasources.RetrofitHelper
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel
import com.juanantbuit.weatherproject.utils.API_KEY
import com.juanantbuit.weatherproject.utils.LANG
import com.juanantbuit.weatherproject.utils.UNITS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ForecastResponseService {

    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun getForecastResponse(latitude: Float?, longitude: Float?): ForecastResponseModel {
        return withContext(Dispatchers.IO) {
            val response: Response<ForecastResponseModel> = retrofit.create(ApiClient::class.java).getForecastResponse(latitude, longitude, API_KEY, UNITS, LANG)
            response.body()!!
        }
    }

}