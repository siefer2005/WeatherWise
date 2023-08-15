package com.juanantbuit.weatherproject.framework.ui.search_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.ranking
import com.algolia.search.dsl.searchableAttributes
import com.algolia.search.dsl.settings
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.response.ResponseSearch
import com.algolia.search.model.search.Query
import com.algolia.search.model.settings.Settings
import com.google.gson.Gson
import com.juanantbuit.weatherproject.BuildConfig
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import kotlinx.coroutines.launch

class SearchListViewModel : ViewModel() {
    private val _searchItemList = MutableLiveData<List<SearchItemModel>?>()
    val searchItemList: LiveData<List<SearchItemModel>?> get() = _searchItemList

    private val applicationID = BuildConfig.ALGOLIA_APP_ID
    private val algoliaAPIKey = BuildConfig.ALGOLIA_KEY
    private val algoliaIndexName = BuildConfig.ALGOLIA_INDEX_NAME

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

    fun getSearchItemList(searchQuery: String) {
        viewModelScope.launch {
            val result: MutableList<SearchItemModel> = arrayListOf()
            val query = Query(searchQuery)

            val responseSearch: ResponseSearch = index.search(query)
            responseSearch.hits.forEach { hit ->
                result.add(
                    Gson().fromJson(hit.json.toString(), SearchItemModel::class.java)
                )
            }
            _searchItemList.postValue(result)
        }
    }
}