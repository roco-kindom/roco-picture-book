package com.lanier.roco.picturebook.feature.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.lanier.roco.picturebook.database.entity.Prop
import com.lanier.roco.picturebook.databinding.ItemPropBinding
import com.lanier.roco.picturebook.ext.gone
import com.lanier.roco.picturebook.ext.visible
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.widget.rv.BaseRvAdapter
import com.lanier.roco.picturebook.widget.rv.BaseVH

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/6 17:23
 */
class PropAdapter : BaseRvAdapter<Prop, PropViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): PropViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPropBinding.inflate(inflater, parent, false)
        return PropViewHolder(binding)
    }
}

class PropViewHolder(private val binding: ItemPropBinding) : BaseVH<Prop>(binding) {

    override fun bind(data: Prop) {
        binding.model = data
        AppData.loadProp(binding.ivPic, data.id)
        if (data.desc.isBlank()) {
            binding.tvDesc.gone()
        } else {
            binding.tvDesc.visible()
        }
    }
}
