package com.juanantbuit.weatherproject.framework.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel
import com.juanantbuit.weatherproject.domain.models.NextDayInfoModel
import com.juanantbuit.weatherproject.usecases.GetCityInfoUseCase
import com.juanantbuit.weatherproject.usecases.GetForecastResponseUseCase
import com.juanantbuit.weatherproject.usecases.GetNextDaysInfoUseCase
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel : ViewModel() {

    private val _currentDay = MutableLiveData<Int?>()
    val currentDay: LiveData<Int?> get() = _currentDay

    private val _cityInfo = MutableLiveData<CityInfoModel?>()
    val cityInfo: LiveData<CityInfoModel?> get() = _cityInfo

    private val _forecastResponse = MutableLiveData<ForecastResponseModel?>()
    val forecastResponse: LiveData<ForecastResponseModel?> get() = _forecastResponse


    private val getCityInfoUseCase = GetCityInfoUseCase()
    private val getForecastResponseUseCase = GetForecastResponseUseCase()
    private val getNextDaysInfoUseCase = GetNextDaysInfoUseCase()

    fun getCurrentDay() {
        val calendar = Calendar.getInstance()
        //Subtraction necessary for the number (1..7) to match the indexes of DAYS_OF_WEEK (0..6)
        val result = calendar[Calendar.DAY_OF_WEEK] - 1
        _currentDay.postValue(result)
    }

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

    //Necessary to stay in the index range of DAYS_OF_WEEK
    fun getCorrectIndex(day: Int) : Int {
        return if(day <= 6) {
            day
        } else {
            day - 7
        }
    }

    fun getNextDaysInfo(forecastResponse: ForecastResponseModel): MutableList<NextDayInfoModel> {

        return getNextDaysInfoUseCase(forecastResponse)

    }

    fun getImageUrl(idIcon: String): String {
        return "https://openweathermap.org/img/wn/$idIcon@4x.png"
    }

}