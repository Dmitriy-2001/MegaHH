package ru.practicum.android.diploma.presentation.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.filter.api.FilterInteractor

class FilterViewModel(private val interactor: FilterInteractor) : ViewModel() {
    private val filterScreenState = MutableLiveData<FilterScreenState>()
    fun getFilterScreenState(): LiveData<FilterScreenState> = filterScreenState

    private val isApplyButtonVisible = MutableLiveData<Boolean>()
    fun getIsApplyButtonVisible(): LiveData<Boolean> = isApplyButtonVisible

    init {
        updateFilterParameters()
    }

    fun isFilterEmpty() = interactor.isFilterEmpty()

    fun saveSalaryToStorage(salary: String) = viewModelScope.launch {
        interactor.setSalaryToStorage(salary)
        isApplyButtonVisible.postValue(true)
    }

    fun clearIndustry() = viewModelScope.launch {
        interactor.setIndustryToStorage(null)
        isApplyButtonVisible.postValue(true)
    }

    fun clearWorkplace() = viewModelScope.launch {
        interactor.setCountryToStorage(null)
        interactor.setRegionToStorage(null)
        isApplyButtonVisible.postValue(true)
    }

    fun saveDoNotShowWithoutSalaryToStorage(doNotShowWithoutSalary: Boolean) = viewModelScope.launch {
        interactor.setDoNotShowWithoutSalaryToStorage(doNotShowWithoutSalary)
        isApplyButtonVisible.postValue(true)
    }

    fun resetFilters() = viewModelScope.launch {
        interactor.resetFilters()
        filterScreenState.postValue(FilterScreenState.NoFilterSelected)
        isApplyButtonVisible.postValue(false)
    }

    fun updateFilterParameters() {
        viewModelScope.launch {
            val filterParams = interactor.getFilterParametersFromStorage()
            if (interactor.isFilterEmpty()) {
                filterScreenState.postValue(FilterScreenState.NoFilterSelected)
            } else {
                filterScreenState.postValue(FilterScreenState.Content(filterParams))
            }
        }
        isApplyButtonVisible.postValue(false)
    }
}
