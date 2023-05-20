package com.juanantbuit.weatherproject.framework.ui.search_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import com.juanantbuit.weatherproject.usecases.GetSearchItemsUseCase
import kotlinx.coroutines.launch

class SearchListViewModel: ViewModel() {

    val getSearchItemsUseCase = GetSearchItemsUseCase()

    private val _searchItemList = MutableLiveData<List<SearchItemModel>?>()
    val searchItemList: LiveData<List<SearchItemModel>?> get() = _searchItemList

    fun getSearchItemList(searchQuery: String) {
        viewModelScope.launch {
            val result = getSearchItemsUseCase(searchQuery)
            _searchItemList.postValue(result)
        }
    }

}