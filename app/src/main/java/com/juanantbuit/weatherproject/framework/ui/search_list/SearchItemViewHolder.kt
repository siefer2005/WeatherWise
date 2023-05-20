package com.juanantbuit.weatherproject.framework.ui.search_list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.juanantbuit.weatherproject.databinding.SearchItemBinding
import com.juanantbuit.weatherproject.domain.models.SearchItemModel

class SearchItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = SearchItemBinding.bind(view)

    fun render(searchItem: SearchItemModel, onClickListener: (SearchItemModel) -> Unit) {
        binding.cityAndCountry.text = searchItem.name + ", " + searchItem.country

        itemView.setOnClickListener { onClickListener(searchItem) }
    }

}