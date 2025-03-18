package ru.practicum.android.diploma.data.search.dto.response

import ru.practicum.android.diploma.data.search.dto.SearchVacancyDto

data class SearchVacanciesResponse(
    val found: Int,
    val perPage: Int,
    val pages: Int,
    val page: Int,
    val items: List<SearchVacancyDto>
)
