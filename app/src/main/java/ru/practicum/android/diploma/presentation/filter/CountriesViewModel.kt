package ru.practicum.android.diploma.presentation.filter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryInteractor
import ru.practicum.android.diploma.domain.filter.api.FilterInteractor
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.search.Resource

class CountriesViewModel(
    private val filterInteractor: FilterInteractor,
    private val filterDictionaryInteractor: FilterDictionaryInteractor
) : ViewModel() {

    private val _selectedCountry = MutableLiveData<FilterParam?>()
    val selectedCountry: LiveData<FilterParam?> = _selectedCountry

    private val _countries = MutableLiveData<List<FilterParam>>()
    val countries: LiveData<List<FilterParam>> = _countries

    private val _screenState = MutableLiveData<CountryScreenState>()
    val screenState: LiveData<CountryScreenState> = _screenState

    init {
        loadCountries()
    }

    fun loadCountries() {
        _screenState.value = CountryScreenState.Loading

        viewModelScope.launch {
            filterDictionaryInteractor.loadCountries().catch { e ->
                Log.e("CountriesVM", "Exception: ${e.message}", e)
                _screenState.postValue(CountryScreenState.Error)
            }.collect { result ->
                handleCountriesResult(result)
            }
        }
    }

    private fun handleCountriesResult(resource: Resource<List<FilterParam>>) {
        when (resource) {
            is Resource.Success -> {
                _countries.postValue(resource.data)
                _screenState.postValue(CountryScreenState.CountriesLoaded)
            }

            is Resource.Error -> {
                Log.e("CountriesVM", "Failed to load countries: ${resource.errorType}")
                _screenState.postValue(CountryScreenState.Error)
            }
        }
    }

    fun selectCountry(country: FilterParam) {
        _selectedCountry.value = country
        filterInteractor.setCountryToStorage(country)
    }
}
