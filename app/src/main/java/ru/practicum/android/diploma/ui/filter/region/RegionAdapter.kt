package ru.practicum.android.diploma.ui.filter.region

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.filter.models.RegionModel

class RegionAdapter(
    private var regionList: List<RegionModel>,
    private var onRegionClick: (RegionModel) -> Unit
) : RecyclerView.Adapter<RegionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_regions, parent, false)
        return RegionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        val region = regionList[position]
        holder.bind(region)
        holder.itemView.setOnClickListener {
            onRegionClick(region)
        }
    }

    override fun getItemCount(): Int {
        return regionList.size
    }

    fun updateRegion(newRegionList: List<RegionModel>) {
        regionList = newRegionList
        notifyDataSetChanged()
    }
}
