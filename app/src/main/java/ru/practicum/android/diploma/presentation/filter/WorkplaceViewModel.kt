package ru.practicum.android.diploma.presentation.filter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryInteractor
import ru.practicum.android.diploma.domain.filter.api.FilterInteractor
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import java.io.IOException

class WorkplaceViewModel(
    private val filterInteractor: FilterInteractor,
    private val filterDictionaryInteractor: FilterDictionaryInteractor
) : ViewModel() {

    private val _selectedCountry = MutableLiveData<FilterParam?>()
    val selectedCountry: LiveData<FilterParam?> = _selectedCountry

    private val _countries = MutableLiveData<List<FilterParam>>()
    val countries: LiveData<List<FilterParam>> = _countries

    private val _state = MutableLiveData<WorkplaceState>()
    val state: LiveData<WorkplaceState> = _state

    init {
        _selectedCountry.value = filterInteractor.getFilterParametersFromStorage().country
    }

    fun loadCountries() {
        _state.value = WorkplaceState.Loading
        viewModelScope.launch {
            try {
                val result = filterDictionaryInteractor.loadCountries()
                _countries.postValue(result)
                _state.postValue(WorkplaceState.CountriesLoaded)
            } catch (e: IOException) {
                Log.e("WorkplaceVM", "Failed to load countries", e)
                _state.postValue(WorkplaceState.Error)
            }
        }
    }

    fun selectCountry(country: FilterParam) {
        _selectedCountry.value = country
        filterInteractor.setCountryToStorage(country)
    }
}
