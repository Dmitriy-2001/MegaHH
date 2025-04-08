package ru.practicum.android.diploma.presentation.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.filter.api.FilterInteractor
import ru.practicum.android.diploma.domain.filter.models.FilterParams

class FilterViewModel(private val interactor: FilterInteractor) : ViewModel() {
    private val filterScreenState = MutableLiveData<FilterScreenState>()
    fun getFilterScreenState(): LiveData<FilterScreenState> = filterScreenState

    private val isApplyButtonVisible = MutableLiveData<Boolean>()
    fun getIsApplyButtonVisible(): LiveData<Boolean> = isApplyButtonVisible

    private val isResetButtonVisible = MutableLiveData<Boolean>()
    fun getIsResetButtonVisible(): LiveData<Boolean> = isResetButtonVisible

    private var startFilterParameters: FilterParams? = null

    init {
        viewModelScope.launch {
            val params = interactor
                .getFilterParametersFromStorage()
            startFilterParameters = params
        }
        updateFilterParameters()
    }

    fun saveSalaryToStorage(salary: String) = viewModelScope.launch {
        interactor.setSalaryToStorage(salary)
        isApplyButtonVisible.postValue(true)
        if (salary.isNotEmpty()) isResetButtonVisible.postValue(true)
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
        if (doNotShowWithoutSalary) isResetButtonVisible.postValue(true)
    }

    fun resetFilters() = viewModelScope.launch {
        interactor.resetFilters()
        filterScreenState.postValue(FilterScreenState.NoFilterSelected)
        isApplyButtonVisible.postValue(true)
        isResetButtonVisible.postValue(false)
    }

    fun updateFilterParameters() {
        viewModelScope.launch {
            val filterParams = interactor.getFilterParametersFromStorage()
            if (interactor.isFilterEmpty()) {
                filterScreenState.postValue(FilterScreenState.NoFilterSelected)
                isResetButtonVisible.postValue(false)
            } else {
                filterScreenState.postValue(FilterScreenState.Content(filterParams))
                isResetButtonVisible.postValue(true)
            }
        }
    }

    fun checkIfFilterParamsUpdated(filterParameters: FilterParams) {
        if (this.startFilterParameters != filterParameters) {
            isApplyButtonVisible.postValue(true)
        }
    }
}
