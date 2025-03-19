package ru.practicum.android.diploma.data.search.dto

import com.google.gson.annotations.SerializedName

data class SearchVacancyDto(
    val id: String,
    val name: String,
    val employer: Employer,
    val address: Address,
    val salary: Salary,
)

data class Employer(
    val name: String,
    @SerializedName("logo_urls")
    val logoUrls: LogoUrls?,
)

data class LogoUrls(
    @SerializedName("240")
    val url240: String = ""
)

data class Address(
    val city: String?
)

data class Salary(
    val currency: String?,
    val from: Int?,
    val to: Int?
)
