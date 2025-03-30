package ru.practicum.android.diploma.data.filter.impl

import ru.practicum.android.diploma.data.search.dto.CountryDto
import ru.practicum.android.diploma.data.search.dto.RegionDto
import ru.practicum.android.diploma.data.search.network.HHApi
import ru.practicum.android.diploma.data.search.network.NetworkClient
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryRepository
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.filter.models.Region

class FilterDictionaryRepositoryImpl(private val networkClient: NetworkClient, private val hhApi: HHApi) :
    FilterDictionaryRepository {
    // Здесь методы получения словарей для фильтрации (регионы, страны, индустрии)

    // получение стран
    override suspend fun getCountries(): List<FilterParam> {
        return hhApi.getCountries().map { it.toFilterParam() }
    }

    // получение регионов
    override suspend fun getRegions(): List<Region> {
        return hhApi.getAllRegions().map { it.toRegion() }
    }

    // Мапперы
    private fun CountryDto.toFilterParam() = FilterParam(
        id = this.id, name = this.name
    )

    private fun RegionDto.toRegion(): Region {
        return Region(
            id = this.id,
            name = this.name,
            parentId = this.parent_id,
            areas = this.areas?.map { it.toRegion() } ?: emptyList())
    }
}
