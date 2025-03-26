package ru.practicum.android.diploma.domain.search.models

data class VacancyModel(
    val id: String,
    val name: String,
    val employer: String,
    val logoUrl: String?,
    val city: String,
    val salary: String,
    val description: String,
    val employmentForm: String?,
    val experience: String,
    val keySkills: List<String> = listOf(),
    val alternateUrl: String
)
