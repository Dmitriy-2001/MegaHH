package ru.practicum.android.diploma.domain.favorites.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.search.models.VacancyModel

interface FavoriteVacanciesInteractor {
    suspend fun addVacancyToFavorite(vacancy: VacancyModel)
    suspend fun removeVacancyFromFavorite(vacancy: VacancyModel)
    fun getVacanciesFavorite(): Flow<List<VacancyModel>>
    fun getVacancyFavoriteById(id: String): Flow<VacancyModel>
    suspend fun checkVacanciesFavorite(id: String): Boolean
}
