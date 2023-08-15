package com.juanantbuit.weatherproject.domain.models

data class NextDayInfoModel (
    val averageTemp: Int,
    val iconId: String,
    val temperatures: MutableList<Double>
)