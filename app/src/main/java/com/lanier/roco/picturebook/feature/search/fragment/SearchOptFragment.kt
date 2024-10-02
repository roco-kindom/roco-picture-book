package com.lanier.roco.picturebook.feature.search.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.databinding.FragmentSearchOptBinding
import com.lanier.roco.picturebook.feature.search.ISearchAction
import com.lanier.roco.picturebook.feature.search.entity.SearchDataType
import com.lanier.roco.picturebook.utils.FragmentSwitchHelper

class SearchOptFragment : Fragment() {

    companion object {

        fun newInstance(
            type: SearchDataType?
        ): SearchOptFragment {
            val args = Bundle()

            val fragment = SearchOptFragment()
            fragment.arguments = args
            fragment.searchDataType = type
            return fragment
        }
    }

    private var searchDataType: SearchDataType? = null
    var onResearchListener: ISearchAction? = null

    private val binding by lazy {
        FragmentSearchOptBinding.inflate(layoutInflater)
    }

    private val tabTitles = listOf("精灵", "技能")

    private val fragmentSwitchHelper by lazy {
        FragmentSwitchHelper(
            resId = R.id.childContentContainer,
            fragmentManager = parentFragmentManager
        )
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
        tabTitles.forEach { tabTitle ->
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().apply {
                    text = tabTitle
                }
            )
        }
        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener)

        fragmentSwitchHelper.setFragments(
            listOf(
                FragmentSwitchHelper.SwitchFragment(
                    fragment = SearchSpiritOptFragment.newInstance(
                        isEnableResearch = searchDataType == SearchDataType.Spirit
                    ).apply {
                        this.onResearchListener = this@SearchOptFragment.onResearchListener
                    }
                ),
                FragmentSwitchHelper.SwitchFragment(
                    fragment = SearchSkillOptFragment.newInstance(
                        isEnableResearch = searchDataType == SearchDataType.Skill
                    ).apply {
                        this.onResearchListener = this@SearchOptFragment.onResearchListener
                    }
                ),
            )
        )

        fragmentSwitchHelper.switchFra(0)
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.tabLayout.removeOnTabSelectedListener(onTabSelectedListener)
    }
}