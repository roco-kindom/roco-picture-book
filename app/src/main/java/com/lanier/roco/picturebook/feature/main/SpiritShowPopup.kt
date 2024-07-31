package com.lanier.roco.picturebook.feature.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Spirit

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
    }

    private val spirit by lazy { arguments?.getParcelable("spirit") as? Spirit }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.popup_spirit_details, container, false)
    }
}