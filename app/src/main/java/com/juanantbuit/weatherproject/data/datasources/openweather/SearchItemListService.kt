package com.juanantbuit.weatherproject.data.datasources.openweather

import com.juanantbuit.weatherproject.data.datasources.RetrofitHelper
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import com.juanantbuit.weatherproject.utils.API_KEY
import com.juanantbuit.weatherproject.utils.LANG
import com.juanantbuit.weatherproject.utils.SEARCH_LIMIT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class SearchItemListService {

    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun getSearchItemList(searchQuery: String): List<SearchItemModel> {
        return withContext(Dispatchers.IO) {
            val response: Response<List<SearchItemModel>> = retrofit.create(ApiClient::class.java).getSearchItemList(searchQuery, SEARCH_LIMIT, API_KEY, LANG)
            response.body()!!
        }
    }

}