package ru.practicum.android.diploma.data.search.network

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.search.Resource

interface NetworkClient {
    fun <R, T> doRequest(dto: R, clazz: Class<T>): Flow<Resource<T>>
}
