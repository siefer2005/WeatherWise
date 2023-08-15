package com.juanantbuit.weatherproject.usecases

import com.juanantbuit.weatherproject.data.repositories.CityInfoRepository
import com.juanantbuit.weatherproject.domain.models.CityInfoModel

class GetCityInfoUseCase {
    private val repository = CityInfoRepository()
    suspend fun getCityInfo(latitude: Float, longitude: Float): CityInfoModel = repository.getCityInfo(latitude, longitude)
    suspend fun getCityInfo(geoId: String): CityInfoModel = repository.getCityInfo(geoId)

}