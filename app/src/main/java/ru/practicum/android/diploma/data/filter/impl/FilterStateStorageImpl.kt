package ru.practicum.android.diploma.data.filter.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.data.filter.storage.FilterStateStorage
import ru.practicum.android.diploma.domain.filter.models.FilterParams

class FilterStateStorageImpl(val prefs: SharedPreferences, val gson: Gson) : FilterStateStorage {

    override fun putFilterParams(filterParams: FilterParams) {
        prefs.edit().putString(FILTER_KEY, gson.toJson(filterParams)).apply()
    }

    override fun getFilterParams() = prefs.getString(FILTER_KEY, null)?.takeIf { it.isNotEmpty() }?.let {
        gson.fromJson(it, FilterParams::class.java)
    } ?: FilterParams()

    override fun removeFilterParams() = prefs.edit().remove(FILTER_KEY).apply()

    companion object {
        private const val FILTER_KEY = "FILTER_KEY"
    }
}
