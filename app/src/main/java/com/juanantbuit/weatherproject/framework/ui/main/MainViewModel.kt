package com.juanantbuit.weatherproject.framework.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel
import com.juanantbuit.weatherproject.usecases.GetCityInfoUseCase
import com.juanantbuit.weatherproject.usecases.GetForecastResponseUseCase
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {


    private val _cityInfo = MutableLiveData<CityInfoModel?>()
    val cityInfo: LiveData<CityInfoModel?> get() = _cityInfo

    private val _forecastResponse = MutableLiveData<ForecastResponseModel?>()
    val forecastResponse: LiveData<ForecastResponseModel?> get() = _forecastResponse

    private val getCityInfoUseCase = GetCityInfoUseCase()
    private val getForecastResponseUseCase = GetForecastResponseUseCase()

    fun getCityInfo(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val result = getCityInfoUseCase.getCityInfo(latitude, longitude)
            _cityInfo.postValue(result)
        }
    }

    fun getForecastResponse(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val result = getForecastResponseUseCase.getForecastResponse(latitude, longitude)
            _forecastResponse.postValue(result)
        }
    }

    fun getImageUrl(): String {
        return "https://openweathermap.org/img/wn/" + cityInfo.value!!.iconId[0].idIcon + "@4x.png"
    }

}