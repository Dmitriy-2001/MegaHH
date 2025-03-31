package ru.practicum.android.diploma.data.filter.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.data.search.dto.request.IndustryRequest
import ru.practicum.android.diploma.data.search.dto.response.IndustryResponse
import ru.practicum.android.diploma.data.search.network.NetworkClient
import ru.practicum.android.diploma.domain.filter.api.FilterDictionaryRepository
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.search.Resource

class FilterDictionaryRepositoryImpl(private val networkClient: NetworkClient) : FilterDictionaryRepository {
    // Здесь методы получения словарей для фильтрации (регионы, страны, индустрии)
    override fun getIndustries(): Flow<Resource<List<FilterParam>>> =
        networkClient.doRequest<IndustryRequest, List<IndustryResponse>>(IndustryRequest)
            .mapResource { response ->
                response.map { industry ->
                    FilterParam(id = industry.id, name = industry.name)
                }
            }


    private inline fun <R, reified T> NetworkClient.doRequest(dto: R): Flow<Resource<T>> {
        return doRequest(dto, T::class.java)
    }

    private fun <T, X> Flow<Resource<T>>.mapResource(transform: (T) -> X): Flow<Resource<X>> =
        map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(transform(resource.data))
                is Resource.Error -> Resource.Error(resource.errorType)
            }
        }
}
