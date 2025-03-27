package ru.practicum.android.diploma.data.favorites.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_vacancies")
data class FavoriteVacancyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val employer: String,
    val logoUrl: String?,
    val city: String,
    val salary: String,
    val description: String,
    val employmentForm: String?,
    val experience: String,
    val keySkills: List<String>,
    val alternateUrl: String,
    val workFormat: String
)
