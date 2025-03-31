package ru.practicum.android.diploma.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryInteractor
import ru.practicum.android.diploma.domain.filter.api.FilterInteractor
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.search.ErrorType
import ru.practicum.android.diploma.domain.search.Resource

class IndustryViewModel(
    private val filterInteractor: FilterInteractor,
    private val filterDictionaryInteractor: FilterDictionaryInteractor
) : ViewModel() {

    private val _selectedIndustry = MutableLiveData<FilterParam?>()
    val selectedIndustry: LiveData<FilterParam?> = _selectedIndustry

    private val _state = MutableLiveData<IndustryScreenState>()
    val state: LiveData<IndustryScreenState> = _state

    private var allIndustries: List<FilterParam> = emptyList()

    init {
        _selectedIndustry.value = filterInteractor.getFilterParametersFromStorage().industry
        getIndustry()
    }

    fun getIndustry() {
        _state.value = IndustryScreenState.Loading
        viewModelScope.launch {
            filterDictionaryInteractor.getIndustries()
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _state.postValue(IndustryScreenState.Content(resource.data))
                        }

                        is Resource.Error -> {
                            when (resource.errorType) {
                                ErrorType.NOT_FOUND -> _state.postValue(IndustryScreenState.NothingFound)
                                ErrorType.NO_INTERNET -> _state.postValue(IndustryScreenState.NoInternet)
                                ErrorType.SERVER_ERROR -> _state.postValue(IndustryScreenState.Error)
                            }
                        }
                    }
                }
        }
    }

    fun selectIndustry(industry: FilterParam) {
        _selectedIndustry.value = industry
        filterInteractor.setIndustryToStorage(industry)
    }

    fun filterIndustries(query: String) {
        val filteredList = if (query.isBlank()) {
            allIndustries
        } else {
            allIndustries.filter { it.name.contains(query, ignoreCase = true) }
        }
        _state.postValue(IndustryScreenState.Content(filteredList))
    }
}


