package ru.practicum.android.diploma.presentation.filter

import ru.practicum.android.diploma.domain.filter.models.RegionModel

sealed interface RegionScreenState {
    data object Loading : RegionScreenState
    data class Content(val data: List<RegionModel>) : RegionScreenState
    data object Error : RegionScreenState
    data object NothingFound : RegionScreenState
    data object NoInternet : RegionScreenState
}
