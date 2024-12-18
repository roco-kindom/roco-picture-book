package com.lanier.roco.picturebook.feature.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Skill
import com.lanier.roco.picturebook.databinding.LayoutCommonRecyclerViewBinding
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.feature.main.viewmodel.MainViewModel
import com.lanier.roco.picturebook.feature.main.adapter.SkillAdapter
import com.lanier.roco.picturebook.widget.rv.OnItemClickListener
import com.lanier.roco.picturebook.widget.rv.OnLoadMoreListener
import com.lanier.roco.picturebook.widget.rv.SimpleVerticalDivider

/**
 * Created by 幻弦让叶
 * on 2024/9/30, at 20:09
 *
 */
class SkillDataFragment : Fragment() {

    companion object {

        fun newInstance(): SkillDataFragment {
            val args = Bundle()

            val fragment = SkillDataFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewmodel by activityViewModels<MainViewModel>()

    private val binding by lazy { LayoutCommonRecyclerViewBinding.inflate(layoutInflater) }

    private val adapter by lazy {
        SkillAdapter().apply {
            onItemClickListener = object : OnItemClickListener<Skill> {
                override fun onItemClick(t: Skill, position: Int) {
                }
            }

            onLoadMoreListener = object : OnLoadMoreListener {
                override fun onLoadMore() {
                    viewmodel.loadSkills()
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

        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.addItemDecoration(
            SimpleVerticalDivider(
                color = requireContext().getColor(R.color.dimo_md_theme_primary),
                thickness = 4,
            )
        )

        launchSafe {
            viewmodel.skillsFlow
                .collect {
                    adapter.isEnd = it.third
                    if (it.first == 1) {
                        adapter.data = it.second
                    } else {
                        adapter.addData(it.second)
                    }
                }
        }
    }
}