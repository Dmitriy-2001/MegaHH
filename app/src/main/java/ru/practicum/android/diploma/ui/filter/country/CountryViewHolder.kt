package ru.practicum.android.diploma.ui.filter.country

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.filter.models.FilterParam

class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val nameText: TextView = itemView.findViewById(R.id.tv_option_name)

    fun bind(param: FilterParam) {
        nameText.text = param.name
    }
}
