package ru.practicum.android.diploma.data.search.dto

data class CountryDto(
    val id: String,
    val name: String
)

data class RegionDto(
    val id: String,
    val name: String,
    val parent_id: String?,
    val areas: List<RegionDto>? = null
)
