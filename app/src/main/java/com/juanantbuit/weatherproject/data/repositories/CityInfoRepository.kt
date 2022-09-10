package com.juanantbuit.weatherproject.data.repositories

import com.juanantbuit.weatherproject.data.datasources.openweather.CityInfoService
import com.juanantbuit.weatherproject.domain.models.CityInfoModel

class CityInfoRepository {

    private val api = CityInfoService()
    suspend fun getCityInfo(latitude: Double?, longitude: Double?): CityInfoModel {
        return api.getCityInfo(latitude, longitude)
    }
}