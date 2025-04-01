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
