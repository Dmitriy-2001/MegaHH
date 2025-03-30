package ru.practicum.android.diploma.domain.filter.models

data class Region(
    val id: String,
    val name: String,
    val parentId: String?,
    val areas: List<Region> = emptyList()
)
