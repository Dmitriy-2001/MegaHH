package ru.practicum.android.diploma.domain.favorites.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesInteractor
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesRepository
import ru.practicum.android.diploma.domain.search.models.VacancyModel

class FavoriteVacanciesInteractorImpl(private val repository: FavoriteVacanciesRepository) :
    FavoriteVacanciesInteractor {
    override suspend fun addVacancyToFavorite(vacancy: VacancyModel) {
        repository.addVacancyToFavorite(vacancy)
    }

    override suspend fun removeVacancyFromFavorite(vacancy: VacancyModel) {
        repository.removeVacancyFromFavorite(vacancy)
    }

    override fun getVacanciesFavorite(): Flow<List<VacancyModel>> {
        return repository.getVacanciesFavorite()
    }

    override fun getVacancyFavoriteById(id: String): Flow<VacancyModel> {
        return repository.getVacancyFavoriteById(id)
    }

    override suspend fun checkVacanciesFavorite(id: String): Boolean {
        return repository.checkVacanciesFavorite(id)
    }
}
