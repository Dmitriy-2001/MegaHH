package ru.practicum.android.diploma.ui.filter.country

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.filter.models.FilterParam

class CountryAdapter(
    private var items: List<FilterParam>,
    private val onClick: (FilterParam) -> Unit
) : RecyclerView.Adapter<CountryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_countries, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<FilterParam>) {
        items = newItems
        notifyDataSetChanged()
    }
}
