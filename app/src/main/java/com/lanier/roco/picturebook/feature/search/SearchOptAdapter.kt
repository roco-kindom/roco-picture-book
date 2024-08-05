package com.lanier.roco.picturebook.feature.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.feature.search.entity.SearchGroup
import com.lanier.roco.picturebook.feature.search.entity.SearchProperty
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.widget.rv.OnItemSelectListener

class SearchGroupAdapter : RecyclerView.Adapter<SearchGroupVH>() {

    private val data = AppData.spiritGroups.values.map { SearchGroup.convertByGroup(it) }.toList()

    var onItemSelectListener: OnItemSelectListener<SearchGroup>? = null

    var currentIndex = -1
        private set

    private fun switchSelected(newIndex: Int) {
        if (currentIndex != -1) {
            if (currentIndex == newIndex) {
                data[currentIndex].selected = !data[currentIndex].selected
                notifyItemChanged(currentIndex)
                return
            } else {
                data[currentIndex].selected = false
                notifyItemChanged(currentIndex)
            }
        }
        currentIndex = newIndex
        data[newIndex].selected = true
        notifyItemChanged(newIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchGroupVH {
        return SearchGroupVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_opt, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SearchGroupVH, position: Int) {
        holder.itemView.setOnClickListener {
            switchSelected(position)
            onItemSelectListener?.onItemSelected(data[position], position, data[position].selected)
        }
        holder.bind(data[position])
    }
}

class SearchGroupVH(view: View) : RecyclerView.ViewHolder(view) {

    private val rootView = view.findViewById<LinearLayout>(R.id.llSearchRoot)
    private val ivProperty = view.findViewById<ShapeableImageView>(R.id.ivProperty)
    private val tvProperty = view.findViewById<TextView>(R.id.tvName)

    fun bind(group: SearchGroup) {
        rootView.isSelected = group.selected
        tvProperty.isSelected = group.selected
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

    private val data = AppData.spiritProperties.values.map { SearchProperty.convertByProperty(it) }.toList()

    var onItemSelectListener: OnItemSelectListener<SearchProperty>? = null

    var currentIndex = -1
        private set

    private fun switchSelected(newIndex: Int) {
        if (currentIndex != -1) {
            if (currentIndex == newIndex) {
                data[currentIndex].selected = !data[currentIndex].selected
                notifyItemChanged(currentIndex)
                return
            } else {
                data[currentIndex].selected = false
                notifyItemChanged(currentIndex)
            }
        }
        currentIndex = newIndex
        data[newIndex].selected = true
        notifyItemChanged(newIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPropertyVH {
        return SearchPropertyVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_opt, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SearchPropertyVH, position: Int) {
        holder.itemView.setOnClickListener {
            switchSelected(position)
            onItemSelectListener?.onItemSelected(data[position], position, data[position].selected)
        }
        holder.bind(data[position])
    }
}

class SearchPropertyVH(view: View) : RecyclerView.ViewHolder(view) {

    private val rootView = view.findViewById<LinearLayout>(R.id.llSearchRoot)
    private val ivProperty = view.findViewById<ShapeableImageView>(R.id.ivProperty)
    private val tvProperty = view.findViewById<TextView>(R.id.tvName)

    fun bind(property: SearchProperty) {
        rootView.isSelected = property.selected
        tvProperty.isSelected = property.selected
        AppData.loadProperty(ivProperty, property.id)
        tvProperty.text = property.name
    }
}
