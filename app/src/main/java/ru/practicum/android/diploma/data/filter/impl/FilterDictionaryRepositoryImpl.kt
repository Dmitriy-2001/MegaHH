package ru.practicum.android.diploma.data.filter.impl

import ru.practicum.android.diploma.data.search.dto.CountryDto
import ru.practicum.android.diploma.data.search.network.HHApi
import ru.practicum.android.diploma.data.search.network.NetworkClient
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryRepository
import ru.practicum.android.diploma.domain.filter.models.FilterParam

class FilterDictionaryRepositoryImpl(private val networkClient: NetworkClient, private val hhApi: HHApi) :
    FilterDictionaryRepository {
    // Здесь методы получения словарей для фильтрации (регионы, страны, индустрии)

    override suspend fun getCountries(): List<FilterParam> {
        return hhApi.getCountries().map { it.toFilterParam() }
    }

    private fun CountryDto.toFilterParam() = FilterParam(
        id = this.id, name = this.name
    )
}
