package ru.practicum.android.diploma.data.search.dto

data class SearchVacancyResponse(
    val found: Int,
    val perPage: Int,
    val pages: Int,
    val page: Int,
    val items: List<SearchVacancyDto>
)
