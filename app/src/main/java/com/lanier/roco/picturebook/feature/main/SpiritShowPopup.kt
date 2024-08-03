package com.lanier.roco.picturebook.feature.main

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.databinding.PopupSpiritDetailsBinding
import com.lanier.roco.picturebook.ext.gone
import com.lanier.roco.picturebook.ext.visible
import com.lanier.roco.picturebook.manager.AppData

class SpiritShowPopup : BottomSheetDialogFragment() {

    companion object {

        fun newInstance(spirit: Spirit): SpiritShowPopup {
            val args = Bundle().apply {
                putParcelable("spirit", spirit)
            }

            val fragment = SpiritShowPopup()
            fragment.arguments = args
            return fragment
        }

        fun show(spirit: Spirit, fragmentManager: FragmentManager) {
            val popup = newInstance(spirit)
            popup.show(fragmentManager, this::class.java.simpleName)
        }
    }

    private val spirit by lazy { arguments?.getParcelable("spirit") as? Spirit }

    private val binding by lazy { PopupSpiritDetailsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogFullScreenTransparentStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { enterAnim(it) }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spirit?.let {
            AppData.loadSpiritAvatar(binding.ivAvatar, it.iconSrc)
            val properties = it.property.split(",")
            if (properties.size == 2) {
                AppData.loadProperty(binding.ivProperty1, properties[0])
                AppData.loadProperty(binding.ivProperty2, properties[1])
                binding.ivProperty2.visible()
            } else {
                AppData.loadProperty(binding.ivProperty1, it.property)
                binding.ivProperty2.gone()
            }
            bindAbility(it)
            setEggGroup(it.group)
            binding.spirit = it
        }
    }

    private fun bindAbility(spirit: Spirit) {
        binding.vAbilityHP.apply {
            modifyProgressColor(Color.parseColor("#99E74738"))
            bind(R.drawable.ic_ability_hp, spirit.sm.toInt())
        }

        binding.vAbilityAttack.apply {
            modifyProgressColor(Color.parseColor("#99AC9B96"))
            bind(R.drawable.ic_ability_wg, spirit.wg.toInt())
        }

        binding.vAbilityDefense.apply {
            modifyProgressColor(Color.parseColor("#9992BDBD"))
            bind(R.drawable.ic_ability_fy, spirit.fy.toInt())
        }

        binding.vAbilityMagicAttack.apply {
            modifyProgressColor(Color.parseColor("#995BACCF"))
            bind(R.drawable.ic_ability_mg, spirit.mg.toInt())
        }

        binding.vAbilityMagicDefense.apply {
            modifyProgressColor(Color.parseColor("#99518AC2"))
            bind(R.drawable.ic_ability_mk, spirit.mk.toInt())
        }

        binding.vAbilitySpeed.apply {
            modifyProgressColor(Color.parseColor("#99B5E1FE"))
            bind(R.drawable.ic_ability_sd, spirit.sd.toInt())
        }
    }

    private fun setEggGroup(groupId: String) {
        binding.ivEggGroup.setImageResource(
            when (groupId) {
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
    }

    private fun enterAnim(view: View) {
        val animation = TranslateAnimation(
            0f, 0f, view.height.toFloat(), 0f
        ).apply {
            duration = 150 // 动画持续时间
            fillAfter = true
        }
        view.startAnimation(animation)
    }
}