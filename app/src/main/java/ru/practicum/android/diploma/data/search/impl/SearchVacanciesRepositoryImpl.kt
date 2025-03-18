package ru.practicum.android.diploma.data.search.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.data.search.network.NetworkClient
import ru.practicum.android.diploma.domain.search.Resource
import ru.practicum.android.diploma.domain.search.api.SearchVacanciesRepository
import ru.practicum.android.diploma.domain.search.models.VacanciesModel

class SearchVacanciesRepositoryImpl(private val networkClient: NetworkClient) : SearchVacanciesRepository {
    override fun searchVacancies(text: String): Flow<Resource<VacanciesModel>> {
        TODO("Not yet implemented")
    }
}
