package ru.practicum.android.diploma.domain.filter.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.search.Resource

interface FilterDictionaryInteractor {
    // Здесь методы получения словарей для фильтрации (регионы, страны, индустрии)
    fun getIndustries(): Flow<Resource<List<FilterParam>>>
}
