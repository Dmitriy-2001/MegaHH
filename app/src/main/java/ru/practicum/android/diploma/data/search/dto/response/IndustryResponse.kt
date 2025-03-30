package ru.practicum.android.diploma.data.search.dto.response

import ru.practicum.android.diploma.data.search.dto.IndustryDto

data class IndustryResponse(
    val id: String,
    val industries: List<IndustryDto>,
    val name: String
)
