package com.juanantbuit.weatherproject.framework.ui.searchList

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
import com.juanantbuit.weatherproject.databinding.SearchListBinding
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchListActivity : AppCompatActivity() {
    private val viewModel by viewModels<SearchListViewModel>()
    private lateinit var binding: SearchListBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var searchType: String

    @FlowPreview
    private val searchQueryFlow = MutableStateFlow("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchListBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        viewModel.initSearchSettings()

        val layoutManager = LinearLayoutManager(this)

        binding.citySearcherList.post {
            binding.citySearcherList.show()
        }

        searchType = intent.getStringExtra("searchType")!!

        binding.citySearcherList.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                finish()
                overridePendingTransition(0, 0)
            }
        }

        binding.citySearcherList.editText.addTextChangedListener(object : TextWatcher {
            @FlowPreview
            override fun afterTextChanged(editableQuery: Editable?) {
                val query = editableQuery.toString()

                if (query.isNotEmpty() || query.isNotBlank()) {
                    viewModel.setLoading()
                    searchQueryFlow.value = query
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })


        lifecycleScope.launch @FlowPreview {
            searchQueryFlow.debounce(1100) // Tiempo de espera después de que el usuario haya dejado de escribir
                .distinctUntilChanged() // Emitir solo si el valor cambia
                .collect { query ->
                    if (query.isNotBlank()) {
                        viewModel.getSearchItemList(query)
                    }
                }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is SearchListUIState.Error -> {
                            hideProgressBar()
                            Toast.makeText(
                                this@SearchListActivity,
                                "Ha ocurrido un error: ${uiState.msg}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        SearchListUIState.Loading -> {
                            binding.recyclerSearch.visibility = View.INVISIBLE
                            showProgressBar()
                        }

                        SearchListUIState.NotFound -> {
                            binding.recyclerSearch.visibility = View.INVISIBLE
                            hideProgressBar()
                        }

                        is SearchListUIState.Success -> {
                            withContext(Dispatchers.Main) {
                                binding.recyclerSearch.adapter = SearchItemsAdapter(
                                    uiState.searchValues.searchItemModelsList,
                                    uiState.searchValues.cityInfoModelsList
                                ) { searchItem ->
                                    onItemSelected(searchItem)
                                }
                            }
                            binding.recyclerSearch.layoutManager = layoutManager

                            hideProgressBar()

                            binding.recyclerSearch.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

    }

    private fun onItemSelected(searchItem: SearchItemModel) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()

        when (searchType) {
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
        editor.putBoolean("lastSavesIsCoordinated", false)
        editor.apply()
        binding.citySearcherList.hide()
    }

    private fun showProgressBar() {
        binding.progressBar2.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar2.visibility = View.INVISIBLE
    }
}
