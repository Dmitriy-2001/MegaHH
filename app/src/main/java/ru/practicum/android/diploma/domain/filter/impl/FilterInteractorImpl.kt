package ru.practicum.android.diploma.domain.filter.impl

import ru.practicum.android.diploma.domain.filter.api.FilterInteractor
import ru.practicum.android.diploma.domain.filter.api.FilterRepository
import ru.practicum.android.diploma.domain.filter.models.FilterParam

class FilterInteractorImpl(private val repository: FilterRepository) : FilterInteractor {
    override fun setIndustryToStorage(industry: FilterParam?) = repository.setIndustryToStorage(industry)

    override fun setCountryToStorage(country: FilterParam?) = repository.setCountryToStorage(country)

    override fun setRegionToStorage(region: FilterParam?) = repository.setRegionToStorage(region)

    override fun setSalaryToStorage(salary: String) = repository.setSalaryToStorage(salary)

    override fun setDoNotShowWithoutSalaryToStorage(doNotShowWithoutSalary: Boolean) =
        repository.setDoNotShowWithoutSalaryToStorage(doNotShowWithoutSalary)

    override fun isFilterEmpty() = repository.isFilterEmpty()

    override fun getFilterParametersFromStorage() = repository.getFilterParametersFromStorage()
    override fun resetFilters() = repository.resetFilters()
}
