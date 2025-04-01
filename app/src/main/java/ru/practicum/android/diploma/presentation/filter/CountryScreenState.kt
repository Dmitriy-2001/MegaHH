package ru.practicum.android.diploma.presentation.filter

sealed interface CountryScreenState {
    data object Loading : CountryScreenState
    data object CountriesLoaded : CountryScreenState
    data object Error : CountryScreenState
}
