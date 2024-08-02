package com.lanier.roco.picturebook.feature.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
            binding.spirit = it
            binding.vAbilityHP.apply {
                modifyProgressColor(Color.parseColor("#FF0000"))
                bind(it.sm.toInt())
            }
            binding.vAbilityAttack.apply {
                modifyProgressColor(Color.parseColor("#00FF00"))
                bind(it.wg.toInt())
            }
        }
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