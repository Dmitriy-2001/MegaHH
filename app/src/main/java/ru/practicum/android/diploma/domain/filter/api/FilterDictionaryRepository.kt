package ru.practicum.android.diploma.domain.filter.api

import ru.practicum.android.diploma.domain.filter.models.FilterParam

interface FilterDictionaryRepository {
    // Здесь методы получения словарей для фильтрации (регионы, страны, индустрии)

    suspend fun getCountries(): List<FilterParam>

}
