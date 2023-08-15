package com.juanantbuit.weatherproject.framework.ui.search_list

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
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

        viewModel.initSearchSettings()

        binding.citySearcherList.post {
            binding.citySearcherList.show()
        }

        searchType = intent.getStringExtra("searchType")!!

        binding.citySearcherList.addTransitionListener { _, _, newState ->
            if(newState == SearchView.TransitionState.HIDDEN) {
                finish()
                overridePendingTransition(0, 0)
            }
        }

        binding.citySearcherList.editText.addTextChangedListener (object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                    val searchQuery: String = s.toString()
                    if (searchQuery.isNotEmpty()) {
                        viewModel.getSearchItemList(searchQuery)
                        showProgressBar()
                    }
                }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })
        viewModel.searchItemList.observe(this) { searchItemList ->
            hideProgressBar()
            binding.recyclerSearch.layoutManager = LinearLayoutManager(this)
            binding.recyclerSearch.adapter =
                SearchItemsAdapter(searchItemList!!) { searchItem ->
                    onItemSelected(searchItem)
                }
        }
    }

    private fun onItemSelected(searchItem: SearchItemModel) {

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()


        when(searchType) {
            "firstSave" -> {
                editor.putString("firstSaveGeoId", searchItem.geonameid)
                editor.putString("firstSaveName", searchItem.name)
            }

            "secondSave" -> {
                editor.putString("secondSaveGeoId", searchItem.geonameid)
                editor.putString("secondSaveName", searchItem.name)
            }

            "thirdSave" -> {
                editor.putString("thirdSaveGeoId", searchItem.geonameid)
                editor.putString("thirdSaveName", searchItem.name)
            }

            else -> {
                editor.putString("lastGeoId", searchItem.geonameid)
                editor.putBoolean("firstAppStart", false)
            }
        }
        editor.apply()
        binding.citySearcherList.hide()
    }

    private fun showProgressBar() {
        binding.progressBar2.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar2.visibility = View.GONE
    }
}