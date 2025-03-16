package ru.practicum.android.diploma.data

import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.domain.models.FilterParams

class SharedPrefsHelper(val prefs: SharedPreferences, val gson: Gson) {

    fun putFilterParams(key: String, filterParams: FilterParams) {
        prefs.edit().putString(key, gson.toJson(filterParams)).apply()
    }

    fun getFilterParams(key: String): FilterParams? = gson.fromJson(prefs.getString(key, ""), FilterParams::class.java)

    fun removeFilterParams(key: String) = prefs.edit().remove(key).apply()
}
