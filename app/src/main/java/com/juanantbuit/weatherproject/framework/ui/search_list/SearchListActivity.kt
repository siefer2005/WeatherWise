package com.juanantbuit.weatherproject.framework.ui.search_list

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanantbuit.weatherproject.databinding.SearchListBinding
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import kotlinx.android.synthetic.main.search_list.*

class SearchListActivity: AppCompatActivity() {

    private val viewModel by viewModels<SearchListViewModel>()
    private lateinit var binding: SearchListBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchListBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()

        citySearcher.isIconified = false

        binding.citySearcher.setOnQueryTextFocusChangeListener { _, isFocused ->

            citySearcher.isIconified = !isFocused
        }

        binding.citySearcher.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextChange(searchQuery: String): Boolean {

                if (searchQuery.isNotEmpty()) {
                    viewModel.getSearchItemList(searchQuery)
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })


        viewModel.searchItemList.observe(this) { searchItemList ->

            binding.recyclerSearch.layoutManager = LinearLayoutManager(this)
            binding.recyclerSearch.adapter = SearchItemsAdapter(searchItemList!!) { searchItem ->
                onItemSelected(searchItem)
            }
        }
    }

    private fun onItemSelected(searchItem: SearchItemModel) {

        editor.putFloat("lastLatitude", searchItem.lat.toFloat())
        editor.putFloat("lastLongitude", searchItem.lon.toFloat())
        editor.apply()
        finish()
    }
}