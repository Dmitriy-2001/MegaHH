package ru.practicum.android.diploma.domain.filter.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.filter.models.RegionModel
import ru.practicum.android.diploma.domain.search.Resource

interface FilterDictionaryRepository {
    fun getRegions(): Flow<Resource<List<RegionModel>>>
    // Здесь методы получения словарей для фильтрации (регионы, страны, индустрии)

    fun getCountries(): Flow<Resource<List<FilterParam>>>
}
