package ru.practicum.android.diploma.presentation.favorites

import ru.practicum.android.diploma.domain.search.models.VacancyModel

sealed interface FavoriteVacancyState {
    data class Content(val data: List<VacancyModel>) : FavoriteVacancyState
    data object Empty : FavoriteVacancyState
    data object Error : FavoriteVacancyState
    data object Loading : FavoriteVacancyState
}
