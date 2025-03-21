package ru.practicum.android.diploma.presentation.vacancy

import ru.practicum.android.diploma.domain.search.models.VacancyModel

sealed interface VacancyState {
    data class Content(val data: VacancyModel) : VacancyState
    data object NothingFound : VacancyState
    data object ServerError : VacancyState
    data object NoInternet : VacancyState
}
