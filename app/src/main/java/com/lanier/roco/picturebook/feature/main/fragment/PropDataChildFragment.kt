package com.lanier.roco.picturebook.feature.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lanier.roco.picturebook.database.entity.Prop
import com.lanier.roco.picturebook.databinding.LayoutCommonRecyclerViewBinding
import com.lanier.roco.picturebook.feature.main.PropAdapter
import com.lanier.roco.picturebook.feature.main.PropType
import com.lanier.roco.picturebook.feature.main.PropViewModel
import com.lanier.roco.picturebook.widget.rv.OnItemClickListener
import com.lanier.roco.picturebook.widget.rv.OnLoadMoreListener

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/6 16:59
 */
class PropDataChildFragment : Fragment() {

    companion object {

        fun newInstance(type: Int): PropDataChildFragment {
            val args = Bundle().apply {
                putInt("type", type)
            }

            val fragment = PropDataChildFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val type: PropType? by lazy {
        val typeInt = arguments?.getInt("type") ?: -1
        when (typeInt) {
            PropType.TYPE_MEDICINE -> PropType.Medicine
            PropType.TYPE_GULU_BALL -> PropType.GuluBall
            PropType.TYPE_EXP -> PropType.Exp
            PropType.TYPE_TALENT_ENDEAVOR -> PropType.TalentAndEndeavor
            PropType.TYPE_SKILL_STONE -> PropType.SkillStone
            PropType.TYPE_SEEDS_CROPS -> PropType.SeedsAndCrops
            else -> null
        }
    }

    private val binding by lazy {
        LayoutCommonRecyclerViewBinding.inflate(layoutInflater)
    }

    private val adapter by lazy {
        PropAdapter().apply {
            onItemClickListener = object : OnItemClickListener<Prop> {
                override fun onItemClick(t: Prop, position: Int) {
                }
            }

            onLoadMoreListener = object : OnLoadMoreListener {
                override fun onLoadMore() {
                    viewmodel.load()
                }
            }
        }
    }

    private val viewmodel by viewModels<PropViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type ?: return
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

        viewmodel.type = type

        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.addItemDecoration(
            DividerItemDecoration(
                requireContext(), DividerItemDecoration.VERTICAL
            )
        )

        viewmodel.props.observe(viewLifecycleOwner) {
            adapter.isEnd = it.third
            if (it.first == 1) {
                adapter.data = it.second
            } else {
                adapter.addData(it.second)
            }
        }
    }
}