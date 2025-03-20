package ru.practicum.android.diploma.data.search.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.data.search.dto.request.GetVacancyDetailsRequest
import ru.practicum.android.diploma.data.search.dto.response.GetVacancyDetailsResponse
import ru.practicum.android.diploma.data.search.network.NetworkClient
import ru.practicum.android.diploma.data.search.network.utils.Convertor.convertToModel
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.VacanciesRepository
import ru.practicum.android.diploma.domain.search.models.VacanciesModel
import ru.practicum.android.diploma.domain.search.models.VacancyModel

class VacanciesRepositoryImpl(private val networkClient: NetworkClient) : VacanciesRepository {
    override fun searchVacancies(text: String): Flow<Resource<VacanciesModel>> {
        TODO("Not yet implemented")
    }

    override fun getVacancyDetailsById(id: String): Flow<Resource<VacancyModel>> =
        networkClient.doRequest<GetVacancyDetailsRequest, GetVacancyDetailsResponse>(GetVacancyDetailsRequest(id))
            .mapResource {
                it.convertToModel()
            }

    private inline fun <R, reified T> NetworkClient.doRequest(dto: R): Flow<Resource<T>> {
        return doRequest(dto, T::class.java)
    }

    private fun <T, X> Flow<Resource<T>>.mapResource(transform: (T) -> X): Flow<Resource<X>> =
        map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(transform(resource.data))
                is Resource.Error -> Resource.Error(resource.message)
            }
        }
}



