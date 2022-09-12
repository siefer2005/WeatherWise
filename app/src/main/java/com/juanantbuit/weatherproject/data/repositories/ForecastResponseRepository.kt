package com.juanantbuit.weatherproject.data.repositories

import com.juanantbuit.weatherproject.data.datasources.openweather.ForecastResponseService
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel

class ForecastResponseRepository {

    private val api = ForecastResponseService()
    suspend fun getForecastResponse(latitude: Float?, longitude: Float?): ForecastResponseModel {
        return api.getForecastResponse(latitude, longitude)
    }

}