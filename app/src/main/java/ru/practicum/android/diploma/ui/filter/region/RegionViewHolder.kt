package ru.practicum.android.diploma.ui.filter.region

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.filter.models.RegionModel

class RegionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val regionTextView: TextView = itemView.findViewById(R.id.region)

    fun bind(region: RegionModel) {
        regionTextView.text = region.name
    }
}
