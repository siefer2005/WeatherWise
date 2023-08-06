package com.juanantbuit.weatherproject.usecases

import com.juanantbuit.weatherproject.data.repositories.SearchItemsRepository
import com.juanantbuit.weatherproject.domain.models.SearchItemModel

class GetSearchItemsUseCase {

    private val repository = SearchItemsRepository()
    suspend operator fun invoke(searchQuery: String): List<SearchItemModel> = repository.getSearchItemList(searchQuery)

}