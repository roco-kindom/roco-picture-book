package com.lanier.roco.picturebook.feature.search.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.databinding.FragmentSearchSkillOptBinding
import com.lanier.roco.picturebook.feature.search.ISearchAction
import com.lanier.roco.picturebook.feature.search.SearchPropertyAdapter
import com.lanier.roco.picturebook.feature.search.SearchViewModel
import com.lanier.roco.picturebook.feature.search.entity.SearchDataType
import com.lanier.roco.picturebook.feature.search.entity.SearchProperty
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.widget.rv.EqualDivider
import com.lanier.roco.picturebook.widget.rv.OnItemSelectListener

class SearchSkillOptFragment : Fragment() {

    companion object {

        fun newInstance(
            isEnableResearch: Boolean
        ): SearchSkillOptFragment {
            val args = Bundle().apply {
                putBoolean("isEnableResearch", isEnableResearch)
            }

            val fragment = SearchSkillOptFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val binding by lazy { FragmentSearchSkillOptBinding.inflate(layoutInflater) }
    private val viewmodel by activityViewModels<SearchViewModel>()

    private val isEnableResearch by lazy {
        arguments?.getBoolean("isEnableResearch")?: false
    }

    var onResearchListener: ISearchAction? = null

    private val propertyAdapter by lazy {
        SearchPropertyAdapter().apply {
            onItemSelectListener = object : OnItemSelectListener<SearchProperty> {
                override fun onItemSelected(data: SearchProperty, position: Int, selected: Boolean) {
                    viewmodel.modifyProperty(if (selected) data.id.toInt() else -1)
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
        binding.rvProperties.adapter = propertyAdapter
        val equalDivider = ContextCompat.getDrawable(requireContext(), R.drawable.equal_divider)!!
        binding.rvProperties.addItemDecoration(EqualDivider(equalDivider, 4))

        binding.btnResearch.isEnabled = AppData.spiritProperties.isNotEmpty() && isEnableResearch

        binding.cbSearchByName.isChecked = AppData.SPData.fuzzyQuerySkillByName

        binding.btnResearch.setOnClickListener {
            onResearchListener?.onResearch(SearchDataType.Skill)
            viewmodel.research()
        }
    }
}