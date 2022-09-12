package com.juanantbuit.weatherproject.domain.models

import com.google.gson.annotations.SerializedName

data class SearchItemListModel (

    @SerializedName("") val searchItems: List<SearchItemModel>

)