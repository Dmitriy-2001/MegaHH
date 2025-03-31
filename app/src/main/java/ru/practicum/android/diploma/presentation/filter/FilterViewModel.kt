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

    fun isFilterEmpty() = interactor.isFilterEmpty()

    fun saveSalaryToStorage(salary: String) = viewModelScope.launch {
        interactor.setSalaryToStorage(salary)
    }

    fun saveDoNotShowWithoutSalaryToStorage(doNotShowWithoutSalary: Boolean) = viewModelScope.launch {
        interactor.setDoNotShowWithoutSalaryToStorage(doNotShowWithoutSalary)
    }

    fun resetFilters() = viewModelScope.launch {
        interactor.resetFilters()
        filterScreenState.postValue(FilterScreenState.NoFilterSelected)
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
