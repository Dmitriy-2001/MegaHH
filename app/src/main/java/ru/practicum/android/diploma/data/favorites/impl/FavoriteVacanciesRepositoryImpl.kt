package ru.practicum.android.diploma.data.favorites.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.favorites.db.dao.FavoriteVacancyDao
import ru.practicum.android.diploma.data.favorites.db.entity.FavoriteVacancyEntity
import ru.practicum.android.diploma.domain.favorites.api.FavoriteVacanciesRepository
import ru.practicum.android.diploma.domain.search.models.VacancyModel

class FavoriteVacanciesRepositoryImpl(private val favoriteVacancyDao: FavoriteVacancyDao) :
    FavoriteVacanciesRepository {
    override suspend fun addVacancyToFavorite(vacancy: VacancyModel) {
        favoriteVacancyDao.insertVacancy(vacancy.mapToFavoriteVacancyEntity())
    }

    override suspend fun removeVacancyFromFavorite(vacancy: VacancyModel) {
        favoriteVacancyDao.deleteVacancy(vacancy.mapToFavoriteVacancyEntity().id)
    }

    override fun getVacanciesFavorite(): Flow<List<VacancyModel>> = flow {
        val vacancies = favoriteVacancyDao.getAllVacancies().map { it.mapToVacancyModel() }
        emit(vacancies)
    }

    override fun getVacancyFavoriteById(id: String): Flow<VacancyModel> = flow {
        val vacancy = favoriteVacancyDao.getVacancyById(id).mapToVacancyModel()
        emit(vacancy)
    }

    override suspend fun checkIfVacancyIsFavorite(id: String): Boolean {
        val favoriteIds = favoriteVacancyDao.getVacanciesFavoriteIds()
        return favoriteIds.contains(id)
    }

    private fun VacancyModel.mapToFavoriteVacancyEntity() = FavoriteVacancyEntity(
        id = this.id,
        name = this.name,
        employer = this.employer,
        logoUrl = this.logoUrl,
        city = this.city,
        salary = this.salary,
        description = this.description,
        employmentForm = this.employmentForm,
        experience = this.experience,
        keySkills = this.keySkills,
        area = this.area
    )

    private fun FavoriteVacancyEntity.mapToVacancyModel() = VacancyModel(
        id = this.id,
        name = this.name,
        employer = this.employer,
        logoUrl = this.logoUrl,
        city = this.city,
        salary = this.salary,
        description = this.description,
        employmentForm = this.employmentForm,
        experience = this.experience,
        keySkills = this.keySkills,
        area = this.area
    )
}
