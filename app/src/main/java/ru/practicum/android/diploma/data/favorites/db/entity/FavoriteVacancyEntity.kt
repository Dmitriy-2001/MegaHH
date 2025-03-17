package ru.practicum.android.diploma.data.favorites.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_vacancies")
data class FavoriteVacancyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val employer: String,
    val employerLogoUrl: String?,
    val area: String?,
    val salaryFrom: Int?,
    val salaryTo: Int?,
    val salaryСurrency: String?,
    val experience: String?,
    val employment: String?,
    val description: String?,
    val keySkills: String?,
)

