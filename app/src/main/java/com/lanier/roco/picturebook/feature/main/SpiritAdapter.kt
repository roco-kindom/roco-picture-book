package com.lanier.roco.picturebook.feature.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.gone
import com.lanier.roco.picturebook.ext.visible
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.widget.rv.OnItemClickListener
import com.lanier.roco.picturebook.widget.rv.OnLoadMoreListener

class SpiritAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val ITEM_VIEW_TYPE = 0
    private val LOADING_VIEW_TYPE = 1
    private val ITEM_VIEW_END = 2

    private val _data = mutableListOf<Spirit>()
    var data: List<Spirit>
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }
        get() = _data

    var onItemClickListener: OnItemClickListener<Spirit>? = null
    var onLoadMoreListener: OnLoadMoreListener? = null

    var isEnd = false

    fun emptyData() {
        isEnd = true
        notifyDataSetChanged()
    }

    fun addData(list: List<Spirit>) {
        val startIndex = itemCount
        _data.addAll(list)
        notifyItemRangeChanged(startIndex, list.size)
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            recyclerView.layoutManager?.let { layoutManager ->
                if (layoutManager is GridLayoutManager) {
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (totalItemCount <= lastVisibleItem + 1) {
                        if (isEnd.not()) {
                            onLoadMoreListener?.onLoadMore()
                        }
                    }
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(onScrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnScrollListener(onScrollListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            LOADING_VIEW_TYPE -> {
                LoadingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_loading, parent, false)
                )
            }
            ITEM_VIEW_TYPE -> {
                SpiritViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_spirit, parent, false)
                )
            }
            else -> {
                LoadEndViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_end, parent, false)
                )
            }
        }
    }

    override fun getItemCount() = _data.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is SpiritViewHolder) {
            holder.bind(_data[position])
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(
                    _data[position],
                    position
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == _data.size || _data.size == 0) {
            return if (isEnd) ITEM_VIEW_END else LOADING_VIEW_TYPE
        }
        return ITEM_VIEW_TYPE
    }
}

class SpiritViewHolder(view: View) : ViewHolder(view) {

    val avatar = view.findViewById<ShapeableImageView>(R.id.ivAvatar)
    val spiritId = view.findViewById<TextView>(R.id.tvID)
    val name = view.findViewById<TextView>(R.id.tvName)
    val property1 = view.findViewById<ShapeableImageView>(R.id.ivProperty1)
    val property2 = view.findViewById<ShapeableImageView>(R.id.ivProperty2)

    fun bind(spirit: Spirit) {
        AppData.loadSpiritAvatar(avatar, spirit.iconSrc)
        spiritId.text = spirit.id
        name.text = spirit.name
        val properties = spirit.property.split(",")
        if (properties.size == 2) {
            AppData.loadProperty(property1, properties[0])
            AppData.loadProperty(property2, properties[1])
            property2.visible()
        } else {
            AppData.loadProperty(property1, spirit.property)
            property2.gone()
        }
    }
}

class LoadingViewHolder(view: View) : ViewHolder(view)

class LoadEndViewHolder(view: View) : ViewHolder(view)
