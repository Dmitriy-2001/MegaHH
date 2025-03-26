package ru.practicum.android.diploma.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.search.models.VacancyModel

class VacancyAdapter(
    private var vacancyList: List<VacancyModel>,
    private var onVacancyClick: (VacancyModel) -> Unit
) : RecyclerView.Adapter<VacancyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vacancy, parent, false)
        return VacancyViewHolder(view)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        val vacancy = vacancyList[position]
        holder.bind(vacancy)
        holder.itemView.setOnClickListener {
            onVacancyClick(vacancy)
        }
    }

    override fun getItemCount(): Int {
        return vacancyList.size
    }

    fun updateVacancy(newVacancyList: List<VacancyModel>) {
        val oldList = vacancyList.toMutableList()
        oldList.addAll(newVacancyList)
        vacancyList = oldList
        notifyDataSetChanged()
    }

    fun clearVacancy() {
        vacancyList = emptyList()
        notifyDataSetChanged()
    }
}
