package com.lanier.roco.picturebook.feature.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Property
import com.lanier.roco.picturebook.database.entity.SpiritGroup
import com.lanier.roco.picturebook.manager.AppData

class SearchGroupAdapter : RecyclerView.Adapter<SearchGroupVH>() {

    private val data = AppData.spiritGroups.values.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchGroupVH {
        return SearchGroupVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_opt, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SearchGroupVH, position: Int) {
        holder.bind(data[position])
    }
}

class SearchGroupVH(view: View) : RecyclerView.ViewHolder(view) {

    private val ivProperty = view.findViewById<ShapeableImageView>(R.id.ivProperty)
    private val tvProperty = view.findViewById<TextView>(R.id.tvName)

    fun bind(group: SpiritGroup) {
        ivProperty.setImageResource(
            when (group.id) {
                "1" -> R.drawable.ic_egg_group_1
                "2" -> R.drawable.ic_egg_group_2
                "4" -> R.drawable.ic_egg_group_4
                "10" -> R.drawable.ic_egg_group_10
                "12" -> R.drawable.ic_egg_group_12
                "13" -> R.drawable.ic_egg_group_13
                "15" -> R.drawable.ic_egg_group_15
                "21" -> R.drawable.ic_egg_group_21
                "29" -> R.drawable.ic_egg_group_29
                else -> R.drawable.ic_egg_group_32
            }
        )
        tvProperty.text = group.name
    }
}

class SearchPropertyAdapter : RecyclerView.Adapter<SearchPropertyVH>() {

    private val data = AppData.spiritProperties.values.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPropertyVH {
        return SearchPropertyVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_opt, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SearchPropertyVH, position: Int) {
        holder.bind(data[position])
    }
}

class SearchPropertyVH(view: View) : RecyclerView.ViewHolder(view) {

    private val ivProperty = view.findViewById<ShapeableImageView>(R.id.ivProperty)
    private val tvProperty = view.findViewById<TextView>(R.id.tvName)

    fun bind(property: Property) {
        AppData.loadProperty(ivProperty, property.id)
        tvProperty.text = property.name
    }
}
