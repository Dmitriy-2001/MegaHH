package ru.practicum.android.diploma.data.network

import kotlinx.coroutines.flow.Flow

interface NetworkClient {
    suspend fun <R, T> doRequest(dto: R): Flow<Resource<T>>
}
