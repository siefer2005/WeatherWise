package com.juanantbuit.weatherproject.data.datasources.openweather

import com.juanantbuit.weatherproject.data.datasources.RetrofitHelper
import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import com.juanantbuit.weatherproject.utils.API_KEY
import com.juanantbuit.weatherproject.utils.UNITS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class CityInfoService {

    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun getCityInfo(latitude: Float?, longitude: Float?): CityInfoModel {
        return withContext(Dispatchers.IO) {
            val response: Response<CityInfoModel> = retrofit.create(ApiClient::class.java).getCityInfo(latitude, longitude, API_KEY, UNITS)
            response.body()!!
        }
    }

}