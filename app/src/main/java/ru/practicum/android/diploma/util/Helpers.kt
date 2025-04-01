package ru.practicum.android.diploma.util

import android.app.Application
import android.content.res.Configuration
import android.view.View

fun List<View>.show() = this.forEach { it.show() }

fun List<View>.gone() = this.forEach {
    it.gone()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun isSystemDarkMode(app: Application): Boolean {
    val darkModeFlag =
        app.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
}
