package com.lanier.roco.picturebook.feature.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.databinding.ItemSpiritBinding
import com.lanier.roco.picturebook.ext.gone
import com.lanier.roco.picturebook.ext.visible
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.widget.rv.BaseRvAdapter
import com.lanier.roco.picturebook.widget.rv.BaseVH

class SpiritAdapter : BaseRvAdapter<Spirit, SpiritViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): SpiritViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSpiritBinding.inflate(inflater, parent, false)
        return SpiritViewHolder(binding)
    }
}

class SpiritViewHolder(private val binding: ItemSpiritBinding) : BaseVH<Spirit>(binding) {

    override fun bind(data: Spirit) {
        binding.model = data
        AppData.loadSpiritAvatar(binding.ivAvatar, data.iconSrc)
        val properties = data.property.split(",")
        if (properties.size == 2) {
            AppData.loadProperty(binding.ivProperty1, properties[0])
            AppData.loadProperty(binding.ivProperty2, properties[1])
            binding.ivProperty2.visible()
        } else {
            AppData.loadProperty(binding.ivProperty1, data.property)
            binding.ivProperty2.gone()
        }
    }
}
