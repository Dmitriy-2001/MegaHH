package ru.practicum.android.diploma.domain.filter.api

import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.filter.models.Region

interface FilterDictionaryInteractor {
    // Здесь методы получения словарей для фильтрации (регионы, страны, индустрии)

    suspend fun loadCountries(): List<FilterParam>
    suspend fun loadRegions(): List<Region>

}
