package ru.practicum.android.diploma.ui.filter

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import ru.practicum.android.diploma.R

fun setIconForButton(button: ImageButton, isEmpty: Boolean) {
    button.setImageResource(if (isEmpty) R.drawable.ic_arrow_forward else R.drawable.ic_clear)
}

fun setColorForHint(context: Context?, inputLayout: TextInputLayout, isEmpty: Boolean) {
    context?.let {
        val gray = ContextCompat.getColorStateList(it, R.color.gray)
        val onPrimary = getColorFromAttr(
            it,
            com.google.android.material.R.attr.colorOnPrimary
        )
        inputLayout.defaultHintTextColor = if (isEmpty) gray else onPrimary
    }
}

private fun getColorFromAttr(context: Context, attr: Int): ColorStateList? {
    val typedValue = TypedValue()
    val theme = context.theme
    if (theme.resolveAttribute(attr, typedValue, true)) {
        return ContextCompat.getColorStateList(context, typedValue.resourceId)
    }
    return null
}
