package ru.practicum.android.diploma.presentation.search

import ru.practicum.android.diploma.domain.search.models.VacanciesModel

sealed  interface SearchScreenState {
    data object Loading : SearchScreenState
    data class Content(val data: VacanciesModel) : SearchScreenState
    data object Error: SearchScreenState
    data object NothingFound : SearchScreenState
    data object NoInternet:SearchScreenState
}
