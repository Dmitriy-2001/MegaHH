package ru.practicum.android.diploma.data.search.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ru.practicum.android.diploma.data.search.dto.request.GetVacancyDetailsRequest
import ru.practicum.android.diploma.data.search.dto.request.SearchVacanciesRequest
import ru.practicum.android.diploma.domain.search.ErrorType
import ru.practicum.android.diploma.domain.search.Resource

class RetrofitNetworkClient(private val vacancyService: HHApi) : NetworkClient {

    // R — это тип входного параметра, который передается в метод doRequest
    // T — это тип данных, который будет возвращен из метода doRequest

    override fun <R, T> doRequest(dto: R, clazz: Class<T>): Flow<Resource<T>> = flow {
        try {
            val response = when (dto) {
                is SearchVacanciesRequest -> vacancyService.searchVacancy(dto.page, dto.perPage, dto.text)
                is GetVacancyDetailsRequest -> vacancyService.getVacancyDetails(dto.id)
                else -> throw IllegalArgumentException("Unknown request type")
            }

            // Обертываем успешный ответ в Resource.Success
            emit(Resource.Success(response as T))
        } catch (e: HttpException) {
            if (e.code() == 404) {
                emit(Resource.Error(ErrorType.NOT_FOUND))
            } else {
                emit(Resource.Error(ErrorType.SERVER_ERROR))
            }
        }
    }
}
