package com.juanantbuit.weatherproject.framework.ui.search_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.juanantbuit.weatherproject.R
import com.juanantbuit.weatherproject.domain.models.SearchItemModel

class SearchItemsAdapter(private val searchItemList: List<SearchItemModel>, private val onClickListener: (SearchItemModel) -> Unit): RecyclerView.Adapter<SearchItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SearchItemViewHolder(layoutInflater.inflate(R.layout.search_item, parent, false))
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val item = searchItemList[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = searchItemList.size

}