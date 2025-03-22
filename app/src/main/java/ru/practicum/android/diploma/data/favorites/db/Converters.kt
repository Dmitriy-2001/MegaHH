package ru.practicum.android.diploma.data.favorites.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromKeySkillsListToString(keySkills: List<String>): String {
        return keySkills.joinToString(",")
    }

    @TypeConverter
    fun toKeySkillsListFromString(keySkillsString: String): List<String> {
        return keySkillsString.split(",")
    }
}
