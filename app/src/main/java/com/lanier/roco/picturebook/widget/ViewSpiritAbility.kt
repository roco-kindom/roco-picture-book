package com.lanier.roco.picturebook.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.databinding.ViewSpiritAbilityValueBinding

class ViewSpiritAbility @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RelativeLayout(context, attributeSet, defStyleAttr) {

    private val binding = ViewSpiritAbilityValueBinding.inflate(LayoutInflater.from(context), this, true)

    fun modifyProgressColor(newColor: Color) {
        modifyProgressColor(newColor.toArgb())
    }

    fun modifyProgressColor(newColor: Int) {
        val paint = Paint().apply {
            color = newColor
        }
        binding.pbAbility.modifyProgressPaint(paint, false)
    }

    fun bind(value: Int, defValue: Int = 200) {
        context?.let {
            binding.apply {
                tvAbilityValue.text = it.getString(R.string.p_ability_value_def_value, "$value", "$defValue")
                pbAbility.progress = value
            }
        }
    }
}