package ru.practicum.android.diploma.domain.filter.models

data class FilterParams(
    val area: FilterParam? = null,
    val country: FilterParam? = null,
    val industry: FilterParam? = null,
    val salary: String? = null,
    val doNotShowWithoutSalary: Boolean? = null
)

data class FilterParam(
    val id: String,
    val name: String
)
