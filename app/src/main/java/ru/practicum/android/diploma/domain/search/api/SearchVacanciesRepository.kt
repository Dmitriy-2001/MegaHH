package ru.practicum.android.diploma.domain.search.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.models.VacanciesModel

interface SearchVacanciesRepository {
    fun searchVacancies(text: String): Flow<Resource<VacanciesModel>>
}
