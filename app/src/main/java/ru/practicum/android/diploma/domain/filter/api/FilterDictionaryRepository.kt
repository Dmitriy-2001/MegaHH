package ru.practicum.android.diploma.domain.filter.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.filter.models.RegionModel
import ru.practicum.android.diploma.domain.search.Resource

interface FilterDictionaryRepository {
    fun getCountries(): Flow<Resource<List<FilterParam>>>
    fun getIndustries(): Flow<Resource<List<FilterParam>>>
    fun getRegions(): Flow<Resource<List<RegionModel>>>
}
