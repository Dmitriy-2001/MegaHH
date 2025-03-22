package ru.practicum.android.diploma.data.favorites.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromKeySkills(keySkills: List<String>): String {
        return keySkills.joinToString(",")
    }

    @TypeConverter
    fun toKeySkills(keySkillsString: String): List<String> {
        return keySkillsString.split(",")
    }
}
