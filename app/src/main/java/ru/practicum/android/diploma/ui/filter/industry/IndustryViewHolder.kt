package ru.practicum.android.diploma.ui.filter.industry

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.filter.models.FilterParam

class IndustryViewHolder(itemView : View):RecyclerView.ViewHolder(itemView) {
    private val industryRadioButton = itemView.findViewById<MaterialRadioButton>(R.id.industry_radio_button)

    fun bind(industry: FilterParam, isSelected: Boolean, onClick: (FilterParam) -> Unit) {
        industryRadioButton.text = industry.name
        industryRadioButton.isChecked = isSelected

        itemView.setOnClickListener {
            onClick(industry)
        }

        industryRadioButton.setOnClickListener {
            onClick(industry)
        }
    }
}
