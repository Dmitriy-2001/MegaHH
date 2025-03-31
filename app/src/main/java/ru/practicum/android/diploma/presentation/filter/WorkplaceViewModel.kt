package ru.practicum.android.diploma.presentation.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.filter.api.FilterInteractor

class WorkplaceViewModel(private val interactor: FilterInteractor) : ViewModel() {
    private val countryFilter = MutableLiveData<String>()
    private val regionFilter = MutableLiveData<String>()
    fun getCountryFilter(): LiveData<String> = countryFilter
    fun getRegionFilter(): LiveData<String> = regionFilter

    init {
        updateFilterParameters()
    }

    fun updateFilterParameters() = viewModelScope.launch {
        val filterParams = interactor.getFilterParametersFromStorage()
        countryFilter.postValue(filterParams.country?.name ?: "")
        regionFilter.postValue(filterParams.area?.name ?: "")
    }

    fun clearCountry() {
        interactor.setCountryToStorage(null)
        countryFilter.postValue("")
    }

    fun clearRegion() {
        interactor.setRegionToStorage(null)
        regionFilter.postValue("")
    }

}
