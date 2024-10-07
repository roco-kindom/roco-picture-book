package com.lanier.roco.picturebook.feature.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Skill
import com.lanier.roco.picturebook.databinding.ItemSkillBinding
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.widget.rv.BaseRvAdapter
import com.lanier.roco.picturebook.widget.rv.BaseVH

/**
 * Created by 幻弦让叶
 * on 2024/9/30, at 20:11
 *
 */
class SkillAdapter : BaseRvAdapter<Skill, SkillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): SkillViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSkillBinding.inflate(inflater, parent, false)
        return SkillViewHolder(binding)
    }
}

class SkillViewHolder(private val binding: ItemSkillBinding) : BaseVH<Skill>(binding) {

    override fun bind(data: Skill) {
        binding.model = data
        AppData.loadProperty(binding.ivProperty, data.property)
        binding.tvDamageType.text = when (data.damageType) {
            "1" -> itemView.context.getString(R.string.p_skill_damage_type_1)
            "2" -> itemView.context.getString(R.string.p_skill_damage_type_2)
            "3" -> itemView.context.getString(R.string.p_skill_damage_type_3)
            else -> ""
        }
    }
}