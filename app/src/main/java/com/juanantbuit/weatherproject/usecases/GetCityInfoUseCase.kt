package com.juanantbuit.weatherproject.usecases

import com.juanantbuit.weatherproject.data.repositories.CityInfoRepository
import com.juanantbuit.weatherproject.domain.models.CityInfoModel

class GetCityInfoUseCase {

    private val repository = CityInfoRepository()
    suspend fun getCityInfo(latitude: Double, longitude: Double): CityInfoModel = repository.getCityInfo(latitude, longitude)

}