package com.lanier.roco.picturebook.feature.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.databinding.FragmentPropBinding
import com.lanier.roco.picturebook.feature.main.PropType
import com.lanier.roco.picturebook.utils.FragmentSwitchHelper

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/6 16:39
 */
class PropDataFragment : Fragment() {

    companion object {

        fun newInstance(): PropDataFragment {
            val args = Bundle()

            val fragment = PropDataFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val binding by lazy {
        FragmentPropBinding.inflate(layoutInflater)
    }

    private val fragmentSwitchHelper by lazy {
        FragmentSwitchHelper(
            resId = R.id.childContentContainer,
            fragmentManager = parentFragmentManager
        )
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            fragmentSwitchHelper.switchFra(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
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
        val tabs = listOf("战斗药剂", "咕噜球", "经验果", "天赋-努力", "技能石", "农场相关")
        tabs.forEach { tabTitle ->
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().apply { text = tabTitle }
            )
        }

        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener)

        fragmentSwitchHelper.setFragments(
            listOf(
                FragmentSwitchHelper.SwitchFragment(
                    fragment = PropDataChildFragment.newInstance(PropType.TYPE_MEDICINE)
                ),
                FragmentSwitchHelper.SwitchFragment(
                    fragment = PropDataChildFragment.newInstance(PropType.TYPE_GULU_BALL)
                ),
                FragmentSwitchHelper.SwitchFragment(
                    fragment = PropDataChildFragment.newInstance(PropType.TYPE_EXP)
                ),
                FragmentSwitchHelper.SwitchFragment(
                    fragment = PropDataChildFragment.newInstance(PropType.TYPE_TALENT_ENDEAVOR)
                ),
                FragmentSwitchHelper.SwitchFragment(
                    fragment = PropDataChildFragment.newInstance(PropType.TYPE_SKILL_STONE)
                ),
                FragmentSwitchHelper.SwitchFragment(
                    fragment = PropDataChildFragment.newInstance(PropType.TYPE_SEEDS_CROPS)
                ),
            )
        )

        fragmentSwitchHelper.switchFra(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.tabLayout.removeOnTabSelectedListener(onTabSelectedListener)
    }
}