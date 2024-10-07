package com.lanier.roco.picturebook.feature.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.databinding.LayoutCommonRecyclerViewBinding
import com.lanier.roco.picturebook.feature.main.viewmodel.MainViewModel
import com.lanier.roco.picturebook.feature.main.adapter.SpiritAdapter
import com.lanier.roco.picturebook.feature.main.SpiritShowPopup
import com.lanier.roco.picturebook.widget.rv.EqualDivider
import com.lanier.roco.picturebook.widget.rv.OnItemClickListener
import com.lanier.roco.picturebook.widget.rv.OnLoadMoreListener

/**
 * Created by 幻弦让叶
 * on 2024/9/30, at 18:36
 *
 */
class SpiritDataFragment private constructor(): Fragment() {

    companion object {

        fun newInstance(): SpiritDataFragment {
            return SpiritDataFragment()
        }
    }

    private val viewmodel by activityViewModels<MainViewModel>()

    private val binding by lazy { LayoutCommonRecyclerViewBinding.inflate(layoutInflater) }

    private val mAdapter by lazy {
        SpiritAdapter().apply {
            onItemClickListener = object : OnItemClickListener<Spirit> {
                override fun onItemClick(t: Spirit, position: Int) {
                    SpiritShowPopup.show(spirit = t, parentFragmentManager)
                }
            }

            onLoadMoreListener = object : OnLoadMoreListener {
                override fun onLoadMore() {
                    viewmodel.loadSpirits()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerview.adapter = mAdapter
        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.equal_divider)
        binding.recyclerview.addItemDecoration(EqualDivider(divider!!, 3))

        viewmodel.spirits.observe(viewLifecycleOwner) {
            mAdapter.isEnd = it.third
            if (it.first == 1) {
                mAdapter.data = it.second
            } else {
                mAdapter.addData(it.second)
            }
        }
    }
}