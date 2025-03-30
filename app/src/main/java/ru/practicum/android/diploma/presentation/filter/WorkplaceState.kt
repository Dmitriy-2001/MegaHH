package ru.practicum.android.diploma.presentation.filter

sealed interface WorkplaceState {
    data object Loading : WorkplaceState
    data object CountriesLoaded : WorkplaceState
    data object RegionsLoaded : WorkplaceState
    data object Error : WorkplaceState
}
