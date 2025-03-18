package ru.practicum.android.diploma.data.search.network

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.search.Resource

interface NetworkClient {
    suspend fun <R, T> doRequest(dto: R): Flow<Resource<T>>
}
