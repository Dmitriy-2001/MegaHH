package ru.practicum.android.diploma.data.search.dto

import com.google.gson.annotations.SerializedName

data class IndustryItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)
