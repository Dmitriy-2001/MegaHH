package ru.practicum.android.diploma.data.filter.storage

import ru.practicum.android.diploma.domain.models.FilterParams

interface FilterStateStorage {
    fun putFilterParams(key: String, filterParams: FilterParams)

    fun getFilterParams(key: String): FilterParams?

    fun removeFilterParams(key: String)
}
