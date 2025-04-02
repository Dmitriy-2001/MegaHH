package ru.practicum.android.diploma.ui.filter.industry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.filter.models.FilterParam

class IndustryAdapter(
    var items: List<FilterParam>,
    private var selectedIndustry: FilterParam?,
    private val onIndustrySelected: (FilterParam) -> Unit
) : RecyclerView.Adapter<IndustryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_industry, parent, false)
        return IndustryViewHolder(view)
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val item = items[position]
       items[position] == selectedIndustry
        holder.bind(
            industry = item,
            isSelected = item.id == selectedIndustry?.id,
            onClick = { selectedIndustry ->
                this.selectedIndustry = selectedIndustry
                onIndustrySelected(selectedIndustry)
                notifyDataSetChanged()
            }
        )
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<FilterParam>, selected: FilterParam?) {
        items = newItems
        selectedIndustry = items.find { it.id == selected?.id }
        notifyDataSetChanged()
    }
}
