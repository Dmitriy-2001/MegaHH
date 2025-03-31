package ru.practicum.android.diploma.domain.filter.models

data class RegionModel(
    val id: String,
    val name: String,
    val parentId: String,
    val countryName: String
)
