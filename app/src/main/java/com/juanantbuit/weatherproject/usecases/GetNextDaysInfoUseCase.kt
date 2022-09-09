package com.juanantbuit.weatherproject.usecases

import com.juanantbuit.weatherproject.domain.models.ForecastInfoModel
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel
import com.juanantbuit.weatherproject.domain.models.NextDayInfoModel
import com.juanantbuit.weatherproject.utils.AFTERNOON_TIME_INDEX
import com.juanantbuit.weatherproject.utils.TRI_HOURS_IN_DAY

class GetNextDaysInfoUseCase {

    operator fun invoke(forecastResponse: ForecastResponseModel): MutableList<NextDayInfoModel> {

        val forecastsForFiveDays: List<List<ForecastInfoModel>> = forecastResponse.forecastInfoModels.chunked(TRI_HOURS_IN_DAY)
        val nextDaysInfo: MutableList<NextDayInfoModel> = arrayListOf()

        for(day in forecastsForFiveDays.indices) {

            val dayTemps = getAllTempsOfDay(forecastsForFiveDays, day)

            nextDaysInfo.add(NextDayInfoModel(dayTemps.average().toInt(),
                                              forecastsForFiveDays[day][AFTERNOON_TIME_INDEX]
                                                  .iconId[0]
                                                  .idIcon))
        }

        return nextDaysInfo

    }

    private fun getAllTempsOfDay(forecastsForFiveDays: List<List<ForecastInfoModel>>, day: Int): MutableList<Double> {

        val dayTemps: MutableList<Double> = arrayListOf()

        for (triHourlyForecast in forecastsForFiveDays[day].indices) {
            dayTemps.add(forecastsForFiveDays[day][triHourlyForecast].mainInfo.temp)
        }

        return dayTemps

    }

}