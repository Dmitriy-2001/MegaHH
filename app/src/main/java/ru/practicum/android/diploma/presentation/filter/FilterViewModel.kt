package ru.practicum.android.diploma.presentation.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.filter.api.FilterInteractor

class FilterViewModel(private val interactor: FilterInteractor) : ViewModel() {
    private val filterScreenState = MutableLiveData<FilterScreenState>()
    private var searchQuery: String? = null

    fun getFilterScreenState(): LiveData<FilterScreenState> = filterScreenState

    init {
        updateFilterParameters()
    }

    fun setSearchQuery(query: String) {
        this.searchQuery = query
    }

    fun getSearchQuery(): String? = searchQuery

    fun isFilterEmpty(): Boolean {
        return interactor.isFilterEmpty()
    }

    fun saveFilters(
        salary: String?,
        doNotShowWithoutSalary: Boolean
    ) = viewModelScope.launch {
        salary?.let {
            if (it.isNotEmpty()) {
                interactor.setSalaryToStorage(it)
            } else {
                interactor.setSalaryToStorage("")
            }
        } ?: interactor.setSalaryToStorage("")

        interactor.setDoNotShowWithoutSalaryToStorage(doNotShowWithoutSalary)
        updateFilterParameters()
    }

    fun resetFilters() = viewModelScope.launch {
        interactor.setIndustryToStorage(null)
        interactor.setCountryToStorage(null)
        interactor.setRegionToStorage(null)
        interactor.setSalaryToStorage("")
        interactor.setDoNotShowWithoutSalaryToStorage(false)
        updateFilterParameters()
    }

    fun updateFilterParameters() = viewModelScope.launch {
        val filterParams = interactor.getFilterParametersFromStorage()
        if (interactor.isFilterEmpty()) {
            filterScreenState.postValue(FilterScreenState.NoFilterSelected)
        } else {
            filterScreenState.postValue(FilterScreenState.Content(filterParams))
        }
    }
}
