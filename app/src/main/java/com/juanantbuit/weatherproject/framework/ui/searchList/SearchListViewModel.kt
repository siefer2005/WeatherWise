package com.juanantbuit.weatherproject.framework.ui.searchList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.query
import com.algolia.search.dsl.ranking
import com.algolia.search.dsl.searchableAttributes
import com.algolia.search.dsl.settings
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.response.ResponseSearch
import com.algolia.search.model.settings.Settings
import com.google.gson.Gson
import com.juanantbuit.weatherproject.BuildConfig
import com.juanantbuit.weatherproject.domain.models.ItemModel
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import com.juanantbuit.weatherproject.usecases.GetCityInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SearchListUIState>(SearchListUIState.NotFound)
    val uiState: StateFlow<SearchListUIState> = _uiState

    private val applicationID = BuildConfig.ALGOLIA_APP_ID
    private val algoliaAPIKey = BuildConfig.ALGOLIA_KEY
    private val algoliaIndexName = BuildConfig.ALGOLIA_INDEX_NAME

    private val getCityInfoUseCase = GetCityInfoUseCase()

    private val client = ClientSearch(
        ApplicationID(applicationID), APIKey(algoliaAPIKey)
    )

    private val index = client.initIndex(IndexName(algoliaIndexName))

    private val settings: Settings = settings {
        searchableAttributes {
            +"name"
        }
        ranking {
            +Attribute
            +Geo
            +Words
            +Filters
            +Exact
        }
        hitsPerPage = 10
    }

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
}
