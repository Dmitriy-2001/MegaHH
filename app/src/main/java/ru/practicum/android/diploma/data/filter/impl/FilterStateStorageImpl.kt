package ru.practicum.android.diploma.data.filter.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.data.filter.storage.FilterStateStorage
import ru.practicum.android.diploma.domain.models.FilterParams

class FilterStateStorageImpl(val prefs: SharedPreferences, val gson: Gson) : FilterStateStorage {

    override fun putFilterParams(key: String, filterParams: FilterParams) {
        prefs.edit().putString(key, gson.toJson(filterParams)).apply()
    }

    override fun getFilterParams(key: String): FilterParams? =
        gson.fromJson(prefs.getString(key, ""), FilterParams::class.java)

    override fun removeFilterParams(key: String) = prefs.edit().remove(key).apply()
}
