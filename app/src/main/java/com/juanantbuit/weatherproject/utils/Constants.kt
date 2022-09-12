package com.juanantbuit.weatherproject.utils

import com.juanantbuit.weatherproject.BuildConfig

const val BASE_URL: String = "https://api.openweathermap.org/"
const val API_KEY: String = BuildConfig.OPENWEATHER_KEY
const val UNITS: String = "metric"
const val TRI_HOURS_IN_DAY: Int = 8
const val AFTERNOON_TIME_INDEX:Int = 4
const val GPS_REQUEST_CODE = 1
const val SEARCH_LIMIT = 5 //Cannot be more than 5
val DAYS_Of_WEEK = arrayOf(

    "Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday"

)