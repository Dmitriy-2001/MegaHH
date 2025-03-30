package ru.practicum.android.diploma.presentation.search

sealed interface IndustryScreenState {
    data object Loading : IndustryScreenState
    data object Content : IndustryScreenState
    data object NothingFound : IndustryScreenState
    data object NoInternet : IndustryScreenState
    data object Error : IndustryScreenState
}
