package com.lanier.roco.picturebook.feature.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.databinding.PopupSpiritDetailsBinding
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spirit?.let {
            AppData.loadSpiritAvatar(binding.ivAvatar, it.iconSrc)
            binding.tvID.text = it.id
            binding.tvName.text = it.name
        }
    }
}