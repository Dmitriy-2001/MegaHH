package ru.practicum.android.diploma.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.search.models.VacancyModel
import ru.practicum.android.diploma.util.dpToPx

class VacancyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val nameAndCityTextView: TextView = itemView.findViewById(R.id.name_and_city)
    private val employerTextView: TextView = itemView.findViewById(R.id.employer)
    private val salaryAndCurrencyTextView: TextView = itemView.findViewById(R.id.salary)
    private val logoUrlImageView: ImageView = itemView.findViewById(R.id.logo_url)

    fun bind(vacancy: VacancyModel) {
        nameAndCityTextView.text = buildString {
            append(vacancy.name)
            append(", ")
            append(vacancy.city)
        }
        employerTextView.text = vacancy.employer
        salaryAndCurrencyTextView.text = vacancy.salary
        val context = itemView.context
        val cornerRadiusPx = dpToPx(12f, context)
        Glide.with(itemView)
            .load(vacancy.logoUrl)
            .placeholder(R.drawable.placeholder_rv)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusPx))
            .into(logoUrlImageView)
    }
}
