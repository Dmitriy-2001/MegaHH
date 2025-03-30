package ru.practicum.android.diploma.domain.filter.api

import ru.practicum.android.diploma.domain.filter.models.FilterParam

interface FilterDictionaryInteractor {
    // Здесь методы получения словарей для фильтрации (регионы, страны, индустрии)

    suspend fun loadCountries(): List<FilterParam>

}
