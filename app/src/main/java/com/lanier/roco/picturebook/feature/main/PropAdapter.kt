package com.lanier.roco.picturebook.feature.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Prop
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.widget.rv.LoadEndViewHolder
import com.lanier.roco.picturebook.widget.rv.LoadingViewHolder
import com.lanier.roco.picturebook.widget.rv.OnItemClickListener
import com.lanier.roco.picturebook.widget.rv.OnLoadMoreListener

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/6 17:23
 */
class PropAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW_TYPE = 0
    private val LOADING_VIEW_TYPE = 1
    private val ITEM_VIEW_END = 2

    private val _data = mutableListOf<Prop>()
    var data: List<Prop>
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }
        get() = _data

    var onItemClickListener: OnItemClickListener<Prop>? = null
    var onLoadMoreListener: OnLoadMoreListener? = null

    var isEnd = false

    fun addData(list: List<Prop>) {
        val startIndex = itemCount
        _data.addAll(list)
        notifyItemRangeChanged(startIndex, list.size)
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            recyclerView.layoutManager?.let { layoutManager ->
                if (layoutManager is LinearLayoutManager) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LOADING_VIEW_TYPE -> {
                LoadingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_loading, parent, false)
                )
            }
            ITEM_VIEW_TYPE -> PropViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_prop, parent, false)
            )
            else -> {
                LoadEndViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_end_vertical, parent, false)
                )
            }
        }
    }

    override fun getItemCount(): Int = _data.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PropViewHolder) {
            holder.bind(_data[position])
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(
                    t = _data[position],
                    position = position
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

class PropViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private val ivPic = view.findViewById<ShapeableImageView>(R.id.ivPic)
    private val tvID = view.findViewById<TextView>(R.id.tvID)
    private val tvName = view.findViewById<TextView>(R.id.tvName)
    private val tvDesc = view.findViewById<TextView>(R.id.tvDesc)

    fun bind(prop: Prop) {
        AppData.loadProp(ivPic, prop.id)
        tvID.text = prop.id
        tvName.text = prop.name
        tvDesc.text = prop.desc.ifBlank { "暂无介绍" }
    }
}
