package com.juanantbuit.weatherproject.framework.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel
import com.juanantbuit.weatherproject.domain.models.NextDayInfoModel
import com.juanantbuit.weatherproject.usecases.GetCityInfoUseCase
import com.juanantbuit.weatherproject.usecases.GetForecastResponseUseCase
import com.juanantbuit.weatherproject.usecases.GetNextDaysInfoUseCase
import com.juanantbuit.weatherproject.usecases.TurnOnGpsUseCase
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class MainViewModel : ViewModel() {

    private val _currentDay = MutableLiveData<Int?>()
    val currentDay: LiveData<Int?> get() = _currentDay

    private val _cityInfo = MutableLiveData<CityInfoModel?>()
    val cityInfo: LiveData<CityInfoModel?> get() = _cityInfo

    private val _forecastResponse = MutableLiveData<ForecastResponseModel?>()
    val forecastResponse: LiveData<ForecastResponseModel?> get() = _forecastResponse

    private val _coordinates = MutableLiveData<HashMap<String, Double>?>()
    val coordinates: LiveData<HashMap<String, Double>?> get() = _coordinates

    private val getCityInfoUseCase = GetCityInfoUseCase()
    private val getForecastResponseUseCase = GetForecastResponseUseCase()
    private val getNextDaysInfoUseCase = GetNextDaysInfoUseCase()

    fun getCurrentDay() {
        val calendar = Calendar.getInstance()
        //Subtraction necessary for the number (1..7) to match the indexes of DAYS_OF_WEEK (0..6)
        val result = calendar[Calendar.DAY_OF_WEEK] - 1
        _currentDay.postValue(result)
    }

    fun getCityInfo(latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            val result = getCityInfoUseCase.getCityInfo(latitude, longitude)
            _cityInfo.postValue(result)
        }
    }

    fun getForecastResponse(latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            val result = getForecastResponseUseCase.getForecastResponse(latitude, longitude)
            _forecastResponse.postValue(result)
        }
    }

    fun getCoordinatesFromGPS(activity: MainActivity) {

        val locationRequest: LocationRequest?
        val coordinates: HashMap<String, Double> = hashMapOf()

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        if (ActivityCompat.checkSelfPermission(activity.baseContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (isGPSEnabled(activity)) {

                LocationServices.getFusedLocationProviderClient(activity)
                    .requestLocationUpdates(locationRequest, object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)

                            LocationServices.getFusedLocationProviderClient(activity)
                                .removeLocationUpdates(this)

                            if (locationResult.locations.size > 0) {

                                val index: Int = locationResult.locations.size - 1
                                val latitude = locationResult.locations[index].latitude
                                val longitude = locationResult.locations[index].longitude

                                coordinates["latitude"] = latitude
                                coordinates["longitude"] = longitude

                                _coordinates.postValue(coordinates)

                            }
                        }
                    }, Looper.getMainLooper())

            } else {
                val turnOnGpsUseCase = TurnOnGpsUseCase(activity)
                turnOnGpsUseCase.turnOnGPS(locationRequest)
            }
        } else {
            activity.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

    }

    private fun isGPSEnabled(activity: MainActivity): Boolean {
        var locationManager: LocationManager? = null

        if (locationManager == null) {
            locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        }

        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
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