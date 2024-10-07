package com.lanier.roco.picturebook.widget.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lanier.roco.picturebook.databinding.ItemEndVerticalBinding
import com.lanier.roco.picturebook.databinding.ItemLoadingVerticalBinding

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/7 15:20
 */
abstract class BaseRvAdapter<T, VH : BaseVH<T>> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_VIEW_TYPE = 0
    val LOADING_VIEW_TYPE = 1
    val ITEM_VIEW_END = 2

    private val _data = mutableListOf<T>()
    var data: List<T>
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }
        get() = _data

    var onItemClickListener: OnItemClickListener<T>? = null
    var onLoadMoreListener: OnLoadMoreListener? = null

    var isEnd = false

    fun addData(list: List<T>) {
        val startIndex = itemCount
        _data.addAll(list)
        notifyItemRangeChanged(startIndex, list.size)
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            recyclerView.layoutManager?.let { layoutManager ->
                when (layoutManager) {
                    is GridLayoutManager -> {
                        callLoadMore(layoutManager)
                    }

                    is LinearLayoutManager -> {
                        callLoadMore(layoutManager)
                    }
                }
            }
        }
    }

    private fun callLoadMore(
        layoutManager: LinearLayoutManager,
    ) {
        val totalItemCount = layoutManager.itemCount
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

        if (totalItemCount <= lastVisibleItem + 1) {
            if (isEnd.not()) {
                onLoadMoreListener?.onLoadMore()
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
                onLoadingHolder(parent)
            }

            ITEM_VIEW_TYPE -> {
                onCreateViewHolder(parent)
            }

            else -> {
                onLoadEndHolder(parent)
            }
        }
    }

    abstract fun onCreateViewHolder(parent: ViewGroup): VH

    protected open fun onLoadEndHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEndVerticalBinding.inflate(inflater, parent, false)
        return LoadEndViewHolder(binding)
    }

    protected open fun onLoadingHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLoadingVerticalBinding.inflate(inflater, parent, false)
        return LoadingViewHolder(binding)
    }

    override fun getItemCount() = _data.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < _data.size) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(
                    _data[position],
                    position
                )
            }
            (holder as? BaseVH<T>)?.bind(_data[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == _data.size || _data.size == 0) {
            return if (isEnd) ITEM_VIEW_END else LOADING_VIEW_TYPE
        }
        return ITEM_VIEW_TYPE
    }
}

abstract class BaseVH<T>(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(data: T)
}

class LoadingViewHolder(vb: ViewBinding) : BaseVH<Unit>(vb) {
    override fun bind(data: Unit) {}
}

class LoadEndViewHolder(vb: ViewBinding) : BaseVH<Unit>(vb) {
    override fun bind(data: Unit) {}
}