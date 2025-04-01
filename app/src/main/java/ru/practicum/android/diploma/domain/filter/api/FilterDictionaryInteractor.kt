package ru.practicum.android.diploma.domain.filter.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.filter.models.RegionModel

interface FilterDictionaryInteractor {
    fun getRegions(): Flow<Resource<List<RegionModel>>>
    fun loadCountries(): Flow<Resource<List<FilterParam>>>
    fun getIndustries(): Flow<Resource<List<FilterParam>>>
}
