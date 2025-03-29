package ru.practicum.android.diploma.domain.filter.api

import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.filter.models.FilterParams

interface FilterRepository {
    fun setIndustryToStorage(industry: FilterParam?)
    fun setCountryToStorage(country: FilterParam?)
    fun setRegionToStorage(region: FilterParam?)
    fun setSalaryToStorage(salary: String)
    fun setDoNotShowWithoutSalaryToStorage(doNotShowWithoutSalary: Boolean)
    fun isFilterEmpty(): Boolean
    fun getFilterParametersFromStorage(): FilterParams
}
