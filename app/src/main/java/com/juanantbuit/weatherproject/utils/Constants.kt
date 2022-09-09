package com.juanantbuit.weatherproject.utils

import com.juanantbuit.weatherproject.BuildConfig

const val BASE_URL: String = "https://api.openweathermap.org/"
const val API_KEY: String = BuildConfig.OPENWEATHER_KEY
const val UNITS: String = "metric"
const val TRI_HOURS_IN_DAY = 8
const val AFTERNOON_TIME_INDEX = 5