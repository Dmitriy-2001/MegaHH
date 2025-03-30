package ru.practicum.android.diploma.domain.filter.impl

import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryInteractor
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryRepository
import ru.practicum.android.diploma.domain.filter.models.FilterParam

class FilterDictionaryInteractorImpl(
    private val repository: FilterDictionaryRepository
) : FilterDictionaryInteractor {

    override suspend fun loadCountries(): List<FilterParam> {
        return repository.getCountries()
    }
}
