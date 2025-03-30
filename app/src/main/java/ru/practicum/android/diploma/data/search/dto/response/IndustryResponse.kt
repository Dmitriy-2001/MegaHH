package ru.practicum.android.diploma.data.search.dto.response

import ru.practicum.android.diploma.data.search.dto.IndustryItemDto

data class IndustryResponse(
    val id: String,
    val industries: List<IndustryItemDto>,
    val name: String
)
