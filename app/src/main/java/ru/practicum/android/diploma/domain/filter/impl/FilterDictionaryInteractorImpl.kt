package ru.practicum.android.diploma.domain.filter.impl

import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryInteractor
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryRepository
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.filter.models.Region

class FilterDictionaryInteractorImpl(
    private val repository: FilterDictionaryRepository
) : FilterDictionaryInteractor {

    override suspend fun loadCountries(): List<FilterParam> {
        return repository.getCountries()
    }

    override suspend fun loadRegions(): List<Region> {
        return repository.getRegions()
    }
}
