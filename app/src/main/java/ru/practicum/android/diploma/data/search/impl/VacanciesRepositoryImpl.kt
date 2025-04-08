package ru.practicum.android.diploma.data.search.impl

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.data.search.dto.request.GetVacancyDetailsRequest
import ru.practicum.android.diploma.data.search.dto.request.SearchVacanciesRequest
import ru.practicum.android.diploma.data.search.dto.response.GetVacancyDetailsResponse
import ru.practicum.android.diploma.data.search.dto.response.SearchVacanciesResponse
import ru.practicum.android.diploma.data.search.network.NetworkClient
import ru.practicum.android.diploma.data.search.network.utils.Convertor.convertToMap
import ru.practicum.android.diploma.data.search.network.utils.Convertor.convertToModel
import ru.practicum.android.diploma.domain.filter.models.FilterParams
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.VacanciesRepository
import ru.practicum.android.diploma.domain.search.models.VacanciesModel
import ru.practicum.android.diploma.domain.search.models.VacancyModel

class VacanciesRepositoryImpl(private val networkClient: NetworkClient, private val context: Context) :
    VacanciesRepository {
    override fun searchVacancies(text: String, page: Int?, filter: FilterParams?): Flow<Resource<VacanciesModel>> =
        networkClient.doRequest<SearchVacanciesRequest, SearchVacanciesResponse>(
            SearchVacanciesRequest(
                text = text,
                page = page,
                filter = filter?.convertToMap() ?: mapOf()
            )
        ).mapResource { it.convertToModel(context) }

    override fun getVacancyDetailsById(id: String): Flow<Resource<VacancyModel>> =
        networkClient.doRequest<GetVacancyDetailsRequest, GetVacancyDetailsResponse>(GetVacancyDetailsRequest(id))
            .mapResource {
                it.convertToModel(context)
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
