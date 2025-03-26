package ru.practicum.android.diploma.data.favorites.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.practicum.android.diploma.data.favorites.db.entity.FavoriteVacancyEntity

@Dao
interface FavoriteVacancyDao {
    @Insert(entity = FavoriteVacancyEntity::class, OnConflictStrategy.REPLACE)
    suspend fun insertVacancy(vacancy: FavoriteVacancyEntity)

    @Query("DELETE FROM favorite_vacancies WHERE id = :id")
    suspend fun deleteVacancy(id: String)

    @Query("SELECT * FROM favorite_vacancies")
    suspend fun getAllVacancies(): List<FavoriteVacancyEntity>

    @Query("SELECT * FROM favorite_vacancies WHERE id = :id")
    suspend fun getVacancyById(id: String): FavoriteVacancyEntity?

    @Query("SELECT id FROM favorite_vacancies")
    suspend fun getVacanciesFavoriteIds(): List<String>
}
