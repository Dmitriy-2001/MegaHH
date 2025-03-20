package ru.practicum.android.diploma.domain.search.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.VacanciesInteractor
import ru.practicum.android.diploma.domain.search.api.VacanciesRepository
import ru.practicum.android.diploma.domain.search.models.VacanciesModel
import ru.practicum.android.diploma.domain.search.models.VacancyModel

class VacanciesInteractorImpl(private val repository: VacanciesRepository) : VacanciesInteractor {
    override fun searchVacancies(text: String): Flow<Resource<VacanciesModel>> {
        TODO("Not yet implemented")
    }

    override fun getVacancyById(id: String): Flow<Resource<VacancyModel>> = repository.getVacancyDetailsById(id)
}
