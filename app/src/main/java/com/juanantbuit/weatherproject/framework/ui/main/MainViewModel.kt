package com.juanantbuit.weatherproject.framework.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel
import com.juanantbuit.weatherproject.domain.models.NextDayInfoModel
import com.juanantbuit.weatherproject.usecases.GetCityInfoUseCase
import com.juanantbuit.weatherproject.usecases.GetForecastResponseUseCase
import com.juanantbuit.weatherproject.usecases.GetNextDaysInfoUseCase
import com.juanantbuit.weatherproject.usecases.TurnOnGpsUseCase
import com.juanantbuit.weatherproject.utils.GPS_REQUEST_CODE
import kotlinx.coroutines.launch
import java.util.*


class MainViewModel : ViewModel() {

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val _currentDay = MutableLiveData<Int?>()
    val currentDay: LiveData<Int?> get() = _currentDay

    private val _cityInfo = MutableLiveData<CityInfoModel?>()
    val cityInfo: LiveData<CityInfoModel?> get() = _cityInfo

    private val _forecastResponse = MutableLiveData<ForecastResponseModel?>()
    val forecastResponse: LiveData<ForecastResponseModel?> get() = _forecastResponse

    private val _coordinates = MutableLiveData<HashMap<String, Float>>()
    val coordinates: LiveData<HashMap<String, Float>> get() = _coordinates

    private val _geonameId = MutableLiveData<String>()
    val geonameId: LiveData<String> get() = _geonameId

    private val getCityInfoUseCase = GetCityInfoUseCase()
    private val getForecastResponseUseCase = GetForecastResponseUseCase()
    private val getNextDaysInfoUseCase = GetNextDaysInfoUseCase()

    fun getCurrentDay() {
        val calendar = Calendar.getInstance()
        //Subtraction needed for the number (1..7) to match the indexes of DAYS_OF_WEEK (0..6)
        val result = calendar[Calendar.DAY_OF_WEEK] - 1
        _currentDay.postValue(result)
    }

    fun getCityInfoByGeonameId(geoId: String) {
        viewModelScope.launch {
            val result = getCityInfoUseCase.getCityInfo(geoId)
            _cityInfo.postValue(result)
        }
    }

    fun getForecastResponseByGeonameId(geoId: String) {
        viewModelScope.launch {
            val result = getForecastResponseUseCase.getForecastResponse(geoId)
            _forecastResponse.postValue(result)
        }
    }

    fun getCityInfoByCoordinates(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            val result = getCityInfoUseCase.getCityInfo(latitude, longitude)
            _cityInfo.postValue(result)
        }
    }

    fun getForecastResponseByCoordinates(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            val result = getForecastResponseUseCase.getForecastResponse(latitude, longitude)
            _forecastResponse.postValue(result)
        }
    }

    fun checkGPSPermission(activity: MainActivity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCoordinatesFromGPS(activity)
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                GPS_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    fun getCoordinatesFromGPS(activity: MainActivity) {
        val coordinates: HashMap<String, Float> = hashMapOf()
        val locationRequestBuilder: LocationRequest.Builder =
            LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 5000)
        locationRequestBuilder.setMinUpdateIntervalMillis(2000)

        val locationRequest: LocationRequest = locationRequestBuilder.build()

        if (isGPSEnabled(activity)) {

            LocationServices.getFusedLocationProviderClient(activity)
                .requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)

                        LocationServices.getFusedLocationProviderClient(activity)
                                .removeLocationUpdates(this)

                        if (locationResult.locations.isNotEmpty()) {

                            val index: Int = locationResult.locations.size - 1
                            val latitude = locationResult.locations[index].latitude.toFloat()
                            val longitude = locationResult.locations[index].longitude.toFloat()

                            coordinates["latitude"] = latitude
                            coordinates["longitude"] = longitude

                            setFirstAppStartFalse(activity)

                            _coordinates.postValue(coordinates)

                        }
                    }
                }, Looper.getMainLooper())
        } else {
            val turnOnGpsUseCase = TurnOnGpsUseCase(activity)
            turnOnGpsUseCase.turnOnGPS(locationRequest)
        }

    }

    fun setCoordinates(lat: Float, long: Float) {
        val coordinates: HashMap<String, Float> = hashMapOf()

        coordinates["latitude"] = lat
        coordinates["longitude"] = long

        _coordinates.postValue(coordinates)
    }

    fun setGeonameId(geoId: String) {
        _geonameId.postValue(geoId)
    }

    private fun isGPSEnabled(activity: MainActivity): Boolean {

        val locationManager: LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
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

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
    }

    private fun setFirstAppStartFalse(activity: MainActivity) {
        prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        editor = prefs.edit()

        editor.putBoolean("firstAppStart", false)
        editor.apply()
    }

}