package com.lanier.roco.picturebook.feature.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.imageview.ShapeableImageView
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Spirit
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

    private lateinit var rootView: View
    private lateinit var ivAvatar: ShapeableImageView
    private lateinit var tvId: TextView
    private lateinit var tvName: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.popup_spirit_details, container, false)
        ivAvatar = rootView.findViewById(R.id.ivAvatar)
        tvId = rootView.findViewById(R.id.tvID)
        tvName = rootView.findViewById(R.id.tvName)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spirit?.let {
            AppData.loadSpiritAvatar(ivAvatar, it.iconSrc)
            tvId.text = it.id
            tvName.text = it.name
        }
    }
}