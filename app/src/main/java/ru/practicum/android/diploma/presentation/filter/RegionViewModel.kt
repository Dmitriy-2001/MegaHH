package ru.practicum.android.diploma.presentation.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryInteractor
import ru.practicum.android.diploma.domain.filter.api.FilterInteractor
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.filter.models.FilterParams
import ru.practicum.android.diploma.domain.filter.models.RegionModel
import ru.practicum.android.diploma.domain.search.ErrorType
import ru.practicum.android.diploma.domain.search.Resource

class RegionViewModel(
    private val filterDictionaryInteractor: FilterDictionaryInteractor,
    private val filterInteractor: FilterInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<RegionScreenState>(RegionScreenState.Loading)
    fun getScreenState(): LiveData<RegionScreenState> = _screenState

    private val filterParams: FilterParams
        get() = filterInteractor.getFilterParametersFromStorage()

    private val parentId: String? = filterParams.country?.id

    init {
        loadRegions()
    }

    fun loadRegions(query: String = "") {
        viewModelScope.launch {
            filterDictionaryInteractor.getRegions()
                .collect { resource ->
                    handleRegionResult(resource, query)
                }
        }
    }

    fun saveFilterParams(region: RegionModel) {
        CoroutineScope(Dispatchers.IO).launch {
            val regionParam = FilterParam(region.id, region.name)
            filterInteractor.setRegionToStorage(regionParam)

            val countryParam = FilterParam(region.parentId, region.countryName)
            filterInteractor.setCountryToStorage(countryParam)
        }
    }

    private fun handleRegionResult(resource: Resource<List<RegionModel>>, query: String) {
        when (resource) {
            is Resource.Success -> {
                val filteredRegions = resource.data.filter { region ->
                    val matchesParentId = parentId?.let { region.parentId == it } ?: true
                    val matchesQuery = if (query.isNotBlank()) {
                        region.name.contains(query, ignoreCase = true)
                    } else {
                        true
                    }
                    matchesParentId && matchesQuery
                }
                _screenState.value = RegionScreenState.Content(filteredRegions)
            }

            is Resource.Error -> {
                _screenState.value = when (resource.errorType) {
                    ErrorType.NOT_FOUND -> RegionScreenState.NothingFound
                    ErrorType.NO_INTERNET -> RegionScreenState.NoInternet
                    ErrorType.SERVER_ERROR -> RegionScreenState.Error
                }
            }
        }
    }
}
