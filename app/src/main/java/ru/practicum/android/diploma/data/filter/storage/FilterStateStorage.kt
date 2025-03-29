package ru.practicum.android.diploma.data.filter.storage

import ru.practicum.android.diploma.domain.filter.models.FilterParams

interface FilterStateStorage {
    fun putFilterParams(filterParams: FilterParams)

    fun getFilterParams(): FilterParams

    fun removeFilterParams()
}
