package ru.practicum.android.diploma.data.filter.impl

import ru.practicum.android.diploma.data.search.network.NetworkClient
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryRepository

class FilterDictionaryRepositoryImpl(private val networkClient: NetworkClient) : FilterDictionaryRepository {
    // Здесь методы получения словарей для фильтрации (регионы, страны, индустрии)
}
