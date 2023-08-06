package com.juanantbuit.weatherproject.framework.ui.search_list

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanantbuit.weatherproject.databinding.SearchListBinding
import com.juanantbuit.weatherproject.domain.models.SearchItemModel

class SearchListActivity: AppCompatActivity() {

    private val viewModel by viewModels<SearchListViewModel>()
    private lateinit var binding: SearchListBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var searchType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchListBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        binding.citySearcher.isIconified = false
        searchType = intent.getStringExtra("searchType")!!

        binding.citySearcher.setOnQueryTextFocusChangeListener { _, isFocused ->
            binding.citySearcher.isIconified = !isFocused
        }

        binding.citySearcher.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextChange(searchQuery: String): Boolean {

                if (searchQuery.isNotEmpty()) {
                    viewModel.getSearchItemList(searchQuery)
                    showProgressBar()
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })


        viewModel.searchItemList.observe(this) { searchItemList ->
            hideProgressBar()
            binding.recyclerSearch.layoutManager = LinearLayoutManager(this)
            binding.recyclerSearch.adapter = SearchItemsAdapter(searchItemList!!) { searchItem ->
                onItemSelected(searchItem)
            }
        }
    }

    private fun onItemSelected(searchItem: SearchItemModel) {

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()

        when(searchType) {
            "firstSave" -> {
                editor.putFloat("firstSaveLatitude", searchItem.lat.toFloat())
                editor.putFloat("firstSaveLongitude", searchItem.lon.toFloat())
                editor.putString("firstSaveName", searchItem.name)
            }

            "secondSave" -> {
                editor.putFloat("secondSaveLatitude", searchItem.lat.toFloat())
                editor.putFloat("secondSaveLongitude", searchItem.lon.toFloat())
                editor.putString("secondSaveName", searchItem.name)
            }

            "thirdSave" -> {
                editor.putFloat("thirdSaveLatitude", searchItem.lat.toFloat())
                editor.putFloat("thirdSaveLongitude", searchItem.lon.toFloat())
                editor.putString("thirdSaveName", searchItem.name)
            }

            else -> {
                editor.putFloat("lastLatitude", searchItem.lat.toFloat())
                editor.putFloat("lastLongitude", searchItem.lon.toFloat())
                editor.putBoolean("firstAppStart", false)
            }
        }

        editor.apply()
        finish()
    }

    private fun showProgressBar() {
        binding.progressBar2.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar2.visibility = View.GONE
    }
}