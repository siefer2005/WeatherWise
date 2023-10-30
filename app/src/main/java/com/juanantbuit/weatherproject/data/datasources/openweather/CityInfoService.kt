package com.juanantbuit.weatherproject.data.datasources.openweather

import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import com.juanantbuit.weatherproject.utils.API_KEY
import com.juanantbuit.weatherproject.utils.LANG
import com.juanantbuit.weatherproject.utils.UNITS
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class CityInfoService @Inject constructor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val api: ApiClient
) {
    suspend fun getCityInfo(latitude: Float, longitude: Float): CityInfoModel {
        return withContext(dispatcher) {
            val response: Response<CityInfoModel> = api.getCityInfo(latitude, longitude, API_KEY, UNITS, LANG)
            response.body()!!
        }
    }

    suspend fun getCityInfo(geoId: String): CityInfoModel? {
        if (geoId.isBlank()) {
            return null
        }
        return withContext(dispatcher) {
            val response: Response<CityInfoModel> = api.getCityInfo(geoId, API_KEY, UNITS, LANG)

            if (response.isSuccessful) {
                val responseBody = response.body()

                if (responseBody != null) {
                    return@withContext responseBody
                } else {
                    return@withContext null
                }
            } else {
                return@withContext null
            }
        }
    }
}