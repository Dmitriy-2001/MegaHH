package ru.practicum.android.diploma.presentation.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.filter.api.FilterInteractor

class FilterViewModel(private val interactor: FilterInteractor) : ViewModel() {
    private val filterScreenState = MutableLiveData<FilterScreenState>()
    fun getFilterScreenState(): LiveData<FilterScreenState> = filterScreenState

    init {
        getFilterParameters()
    }

    fun saveSalaryToFilter(salary: String) = interactor.setSalaryToStorage(salary)

    fun saveDoNotShowWithoutSalaryToFilter(doNotShowWithoutSalary: Boolean) =
        interactor.setDoNotShowWithoutSalaryToStorage(doNotShowWithoutSalary)

    fun getFilterParameters() {
        val filterParams = interactor.getFilterParametersFromStorage()
        if (interactor.isFilterEmpty()) {
            filterScreenState.postValue(FilterScreenState.NoFilterSelected)
        } else {
            filterScreenState.postValue(FilterScreenState.Content(filterParams))
        }
    }
}
