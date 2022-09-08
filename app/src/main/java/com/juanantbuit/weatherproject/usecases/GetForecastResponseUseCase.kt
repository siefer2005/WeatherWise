package com.juanantbuit.weatherproject.usecases

import com.juanantbuit.weatherproject.data.repositories.ForecastResponseRepository
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel

class GetForecastResponseUseCase {

    private val repository = ForecastResponseRepository()
    suspend fun getForecastResponse(latitude: Double, longitude: Double): ForecastResponseModel = repository.getForecastResponse(latitude, longitude)

}