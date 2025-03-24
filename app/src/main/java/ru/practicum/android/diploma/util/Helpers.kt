package ru.practicum.android.diploma.util

import android.view.View

fun List<View>.setVisibility(isVisible: Boolean) {
    val visibility = if (isVisible) View.VISIBLE else View.GONE
    this.forEach { it.visibility = visibility }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}
