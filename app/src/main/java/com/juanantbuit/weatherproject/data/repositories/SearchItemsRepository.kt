package com.juanantbuit.weatherproject.data.repositories

import com.juanantbuit.weatherproject.data.datasources.openweather.SearchItemListService
import com.juanantbuit.weatherproject.domain.models.SearchItemListModel
import com.juanantbuit.weatherproject.domain.models.SearchItemModel

class SearchItemsRepository {

    private val api = SearchItemListService()
    suspend fun getSearchItemList(searchQuery: String): List<SearchItemModel> {
        return api.getSearchItemList(searchQuery)
    }

}