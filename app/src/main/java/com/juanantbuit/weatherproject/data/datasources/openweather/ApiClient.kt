package com.juanantbuit.weatherproject.data.datasources.openweather

import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel
import com.juanantbuit.weatherproject.domain.models.SearchItemListModel
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {

    @GET("/data/2.5/weather")
    suspend fun getCityInfo(@Query("lat") latitude: Float?,
                            @Query("lon") longitude: Float?,
                            @Query("appid") apiKey: String,
                            @Query("units") units: String)
                            :Response<CityInfoModel>

    @GET("/data/2.5/forecast")
    suspend fun getForecastResponse(@Query("lat") latitude: Float?,
                                    @Query("lon") longitude: Float?,
                                    @Query("appid") apiKey: String,
                                    @Query("units") units: String)
                                :Response<ForecastResponseModel>

    @GET("/geo/1.0/direct")
    suspend fun getSearchItemList(@Query("q") searchText: String,
                                  @Query("limit") searchLimit: Int,
                                  @Query("appid") apiKey: String)
            :Response<List<SearchItemModel>>

}