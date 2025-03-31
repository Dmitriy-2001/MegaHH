package ru.practicum.android.diploma.data.filter.dto

import com.google.gson.annotations.SerializedName

data class RegionDto(
    val id: String,
    val name: String,
    @SerializedName("parent_id")
    val parentId: String?,
    val areas: List<RegionDto>
)
