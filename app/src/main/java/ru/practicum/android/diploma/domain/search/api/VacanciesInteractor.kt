package ru.practicum.android.diploma.domain.search.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.filter.models.FilterParams
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.models.VacanciesModel
import ru.practicum.android.diploma.domain.search.models.VacancyModel

interface VacanciesInteractor {
    fun searchVacancies(text: String, page: Int?, filter: FilterParams?): Flow<Resource<VacanciesModel>>
    fun getVacancyById(id: String): Flow<Resource<VacancyModel>>
}
