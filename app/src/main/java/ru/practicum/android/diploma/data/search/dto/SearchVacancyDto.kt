package ru.practicum.android.diploma.data.search.dto

data class SearchVacancyDto(
    val id: String,
    val name: String,
    val employer: String,
    val employerLogoUrl: String?,
    val area: String?,
    val salaryFrom: Int?,
    val salaryTo: Int?,
    val currency: String?,
    val experience: String?,
    val employment: String?,
    val description: String?,
    val keySkills: String?,
)
