package com.juanantbuit.weatherproject.framework.ui.searchList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.search.client.Index
import com.algolia.search.dsl.query
import com.algolia.search.model.response.ResponseSearch
import com.algolia.search.model.settings.Settings
import com.google.gson.Gson
import com.juanantbuit.weatherproject.domain.models.ItemModel
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import com.juanantbuit.weatherproject.framework.helpers.DataStoreHelper
import com.juanantbuit.weatherproject.usecases.GetCityInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchListViewModel @Inject constructor(
    private val getCityInfoUseCase: GetCityInfoUseCase,
    private val dataStoreHelper: DataStoreHelper,
    private val index: Index,
    private val settings: Settings
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchListUIState>(SearchListUIState.NotFound)
    val uiState: StateFlow<SearchListUIState> = _uiState

    fun initSearchSettings() {
        viewModelScope.launch {
            index.setSettings(settings)
        }
    }

    suspend fun getSearchItemList(searchQuery: String) {
        _uiState.value = SearchListUIState.Loading
        val result: MutableList<SearchItemModel> = arrayListOf()
        val query = query(searchQuery) {
            page = 0
        }

        val responseSearch: ResponseSearch = withContext(Dispatchers.IO) {
            index.search(query)
        }

        if (responseSearch.nbHits != 0) {
            responseSearch.hits.forEach { hit ->
                result.add(
                    Gson().fromJson(hit.json.toString(), SearchItemModel::class.java)
                )
            }
            val cityInfoList = withContext(Dispatchers.IO) {
                getCityInfoUseCase.getCityInfoList(result)
            }
            val itemModelsLists = ItemModel(result, cityInfoList)

            _uiState.value = SearchListUIState.Success(itemModelsLists)
        } else {
            _uiState.value = SearchListUIState.NotFound
        }
    }

    fun setLoading() {
        _uiState.value = SearchListUIState.Loading
    }

    fun setNotFound() {
        _uiState.value = SearchListUIState.NotFound
    }

    fun saveFirstSaveName(firstSaveName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.writeFirstSaveName(firstSaveName)
        }
    }

    fun saveSecondSaveName(secondSaveName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.writeSecondSaveName(secondSaveName)
        }
    }

    fun saveThirdSaveName(thirdSaveName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.writeThirdSaveName(thirdSaveName)
        }
    }

    fun saveLastGeonameId(geoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.writeLastGeonameId(geoId)
        }
    }

    fun saveGeonameId(geoIdKey:String, geoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.writeGeonameId(geoIdKey, geoId)
        }
    }

    fun saveLastSavedIsCoordinated(lastSavedIsCoordinated: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.writeLastSavedIsCoordinated(lastSavedIsCoordinated)
        }
    }

    fun saveIsFirstAppStar(isFirstAppStart: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.writeIsFirstAppStart(isFirstAppStart)
        }
    }


}
