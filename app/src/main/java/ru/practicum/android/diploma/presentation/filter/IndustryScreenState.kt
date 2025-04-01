package ru.practicum.android.diploma.presentation.filter

import ru.practicum.android.diploma.domain.filter.models.FilterParam

sealed interface IndustryScreenState {
    data object Loading : IndustryScreenState
    data class Content(val data: List<FilterParam>) : IndustryScreenState
    data object NothingFound : IndustryScreenState
    data object NoInternet : IndustryScreenState
    data object Error : IndustryScreenState
}
