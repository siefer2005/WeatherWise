package com.juanantbuit.weatherproject.data.repositories

import com.juanantbuit.weatherproject.data.datasources.openweather.ForecastResponseService
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel

class ForecastResponseRepository {

    private val api = ForecastResponseService()
    suspend fun getForecastResponse(latitude: Double?, longitude: Double?): ForecastResponseModel {
        return api.getForecastResponse(latitude, longitude)
    }

}