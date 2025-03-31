package ru.practicum.android.diploma.domain.filter.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.filter.models.RegionModel
import ru.practicum.android.diploma.domain.search.Resource

import ru.practicum.android.diploma.domain.filter.models.FilterParam

interface FilterDictionaryInteractor {
    fun getRegions(): Flow<Resource<List<RegionModel>>>
    // Здесь методы получения словарей для фильтрации (регионы, страны, индустрии)

    suspend fun loadCountries(): List<FilterParam>

}
