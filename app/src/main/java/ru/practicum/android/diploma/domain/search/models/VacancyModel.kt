package ru.practicum.android.diploma.domain.search.models

data class VacancyModel(
    val id: String,
    val name: String,
    val employer: String,
    val logoUrl: String?,
    val city: String?,
    val salary: String,
    val currency: String?
)
