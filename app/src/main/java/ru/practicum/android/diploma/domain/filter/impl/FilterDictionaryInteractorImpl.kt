package ru.practicum.android.diploma.domain.filter.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryInteractor
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryRepository
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.filter.models.RegionModel
import ru.practicum.android.diploma.domain.search.Resource

class FilterDictionaryInteractorImpl(
    private val repository: FilterDictionaryRepository
) : FilterDictionaryInteractor {

    override fun loadCountries(): Flow<Resource<List<FilterParam>>> = repository.getCountries()

    override fun getIndustries(): Flow<Resource<List<FilterParam>>> = repository.getIndustries()

    override fun getRegions(): Flow<Resource<List<RegionModel>>> = repository.getRegions()
}
