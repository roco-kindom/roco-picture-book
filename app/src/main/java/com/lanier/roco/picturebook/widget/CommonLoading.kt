package com.lanier.roco.picturebook.widget

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.lanier.roco.picturebook.R

/**
 * Created by 幻弦让叶
 * Date 2024/4/5 15:57
 */
class CommonLoading : DialogFragment() {

    companion object {

        fun newInstance(cancelable: Boolean = true): CommonLoading {
            val args = Bundle().apply {
                putBoolean("cancelable", cancelable)
            }

            val fragment = CommonLoading()
            fragment.arguments = args
            return fragment
        }
    }

    private val cancelable by lazy { arguments?.getBoolean("cancelable", true)?: true }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_common_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = cancelable
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        dialog?.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
    }
}