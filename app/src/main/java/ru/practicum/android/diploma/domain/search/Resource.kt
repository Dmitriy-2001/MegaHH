package ru.practicum.android.diploma.domain.search

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val errorType: ErrorType) : Resource<Nothing>()
}

enum class ErrorType { NOT_FOUND, NO_INTERNET, SERVER_ERROR }
