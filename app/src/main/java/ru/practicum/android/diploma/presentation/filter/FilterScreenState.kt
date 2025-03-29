package ru.practicum.android.diploma.presentation.filter

import ru.practicum.android.diploma.domain.filter.models.FilterParams

sealed interface FilterScreenState {
    data class Content(val filterParams: FilterParams) : FilterScreenState
    data object NoFilterSelected : FilterScreenState
}
