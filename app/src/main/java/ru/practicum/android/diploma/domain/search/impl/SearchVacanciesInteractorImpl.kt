package ru.practicum.android.diploma.domain.search.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.SearchVacanciesInteractor
import ru.practicum.android.diploma.domain.search.api.SearchVacanciesRepository
import ru.practicum.android.diploma.domain.search.models.VacanciesModel

class SearchVacanciesInteractorImpl(private val repository: SearchVacanciesRepository) : SearchVacanciesInteractor {
    override fun searchVacancies(text: String): Flow<Resource<VacanciesModel>> {
        TODO("Not yet implemented")
    }
}
