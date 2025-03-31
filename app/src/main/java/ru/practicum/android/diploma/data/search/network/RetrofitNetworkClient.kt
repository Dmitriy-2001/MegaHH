package ru.practicum.android.diploma.data.search.network

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ru.practicum.android.diploma.data.filter.dto.request.GetRegionRequest
import ru.practicum.android.diploma.data.search.dto.request.GetVacancyDetailsRequest
import ru.practicum.android.diploma.data.filter.dto.request.IndustryRequest
import ru.practicum.android.diploma.data.search.dto.request.SearchVacanciesRequest
import ru.practicum.android.diploma.data.search.network.utils.checkInternetConnection
import ru.practicum.android.diploma.domain.search.ErrorType
import ru.practicum.android.diploma.domain.search.Resource

class RetrofitNetworkClient(private val vacancyService: HHApi, private val context: Context) : NetworkClient {

    // R — это тип входного параметра, который передается в метод doRequest
    // T — это тип данных, который будет возвращен из метода doRequest

    override fun <R, T> doRequest(dto: R, clazz: Class<T>): Flow<Resource<T>> = flow {
        if (!checkInternetConnection(context)) {
            emit(Resource.Error(ErrorType.NO_INTERNET))
        } else {
            try {
                val response = when (dto) {
                    is SearchVacanciesRequest -> vacancyService.searchVacancy(
                        dto.page,
                        dto.perPage,
                        dto.text,
                        dto.filter
                    )

                    is GetVacancyDetailsRequest -> vacancyService.getVacancyDetails(dto.id)
                    is IndustryRequest -> vacancyService.getIndustries()
                    is GetRegionRequest -> vacancyService.getAreas()

                    else -> throw IllegalArgumentException("Unknown request type")
                }

                emit(Resource.Success(response as T))
            } catch (e: HttpException) {
                val errorType = if (e.code() == NOT_FOUND_CODE) ErrorType.NOT_FOUND else ErrorType.SERVER_ERROR
                emit(Resource.Error(errorType))
            }
        }
    }

    companion object {
        private const val NOT_FOUND_CODE = 404
    }
}
