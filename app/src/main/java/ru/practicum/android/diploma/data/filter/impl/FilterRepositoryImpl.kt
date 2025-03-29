package ru.practicum.android.diploma.data.filter.impl

import ru.practicum.android.diploma.data.filter.storage.FilterStateStorage
import ru.practicum.android.diploma.domain.filter.api.FilterRepository
import ru.practicum.android.diploma.domain.filter.models.FilterParam
import ru.practicum.android.diploma.domain.filter.models.FilterParams

class FilterRepositoryImpl(
    private val filterStorage: FilterStateStorage,
) : FilterRepository {
    override fun setIndustryToStorage(industry: FilterParam?) {
        filterStorage.getFilterParams().copy(
            industry = industry
        ).also { filterStorage.putFilterParams(it) }
    }

    override fun setCountryToStorage(country: FilterParam?) {
        filterStorage.getFilterParams().copy(
            country = country
        ).also { filterStorage.putFilterParams(it) }
    }

    override fun setRegionToStorage(region: FilterParam?) {
        filterStorage.getFilterParams().copy(
            area = region
        ).also { filterStorage.putFilterParams(it) }
    }

    override fun setSalaryToStorage(salary: String) {
        filterStorage.getFilterParams().copy(salary = salary.ifEmpty { null })
            .also { filterStorage.putFilterParams(it) }
    }

    override fun setDoNotShowWithoutSalaryToStorage(doNotShowWithoutSalary: Boolean) {
        filterStorage.getFilterParams().copy(doNotShowWithoutSalary = doNotShowWithoutSalary)
            .also { filterStorage.putFilterParams(it) }
    }

    override fun isFilterEmpty() = filterStorage.getFilterParams() == FilterParams()

    override fun getFilterParametersFromStorage(): FilterParams = filterStorage.getFilterParams()
}
