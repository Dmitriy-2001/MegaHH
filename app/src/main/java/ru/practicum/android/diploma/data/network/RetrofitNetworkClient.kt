package ru.practicum.android.diploma.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ru.practicum.android.diploma.data.search.dto.SearchRequest
import java.io.IOException

class RetrofitNetworkClient(private val vacancyService: HHApi) : NetworkClient {

    // R — это тип входного параметра, который передается в метод doRequest
    // T — это тип данных, который будет возвращен из метода doRequest

    override suspend fun <R, T> doRequest(dto: R): Flow<Resource<T>> = flow {
        try {
            val response = when (dto) {
                is SearchRequest -> {
                    vacancyService.searchVacancy(dto.query)
                }

                else -> throw IllegalArgumentException("Unknown request type")
            }

            // Обертываем успешный ответ в Resource.Success
            emit(Resource.Success(response as T))

        } catch (e: Throwable) {
            // Обрабатываем ошибку и оборачиваем в Resource.Error
            when (e) {
                is IOException -> {
                    // Обработка ошибок ввода/вывода
                    emit(Resource.Error(e))
                }
                is HttpException -> {
                    // Обработка ошибок HTTP
                    emit(Resource.Error(e))
                }
                else -> {
                    // Общая ошибка
                    emit(Resource.Error(e))
                }
            }
        }
    }
}
