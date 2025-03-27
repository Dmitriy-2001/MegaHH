package ru.practicum.android.diploma.data.search.dto.request

data class SearchVacanciesRequest(val page: Int? = null, val perPage: Int? = 20, val text: String)
