package ru.practicum.android.diploma.domain.search.models

data class VacanciesModel(
    val pages: Int,
    val itemsCount: Int,
    val currentPage: Int,
    val items: List<VacancyModel>?
)
