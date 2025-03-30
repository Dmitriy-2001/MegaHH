package ru.practicum.android.diploma.data.search.dto

import com.google.gson.annotations.SerializedName

data class IndustryDto(
    @SerializedName("id") val id: String,
    @SerializedName("industries") val industries: List<IndustryItemDto>,
    @SerializedName("name") val name: String
)
