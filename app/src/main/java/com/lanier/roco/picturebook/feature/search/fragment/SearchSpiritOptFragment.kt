package com.lanier.roco.picturebook.feature.search.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.databinding.FragmentSearchSpiritOptBinding
import com.lanier.roco.picturebook.feature.search.ISearchAction
import com.lanier.roco.picturebook.feature.search.SearchGroupAdapter
import com.lanier.roco.picturebook.feature.search.SearchPropertyAdapter
import com.lanier.roco.picturebook.feature.search.SearchViewModel
import com.lanier.roco.picturebook.feature.search.entity.SearchDataType
import com.lanier.roco.picturebook.feature.search.entity.SearchGroup
import com.lanier.roco.picturebook.feature.search.entity.SearchProperty
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.widget.rv.EqualDivider
import com.lanier.roco.picturebook.widget.rv.OnItemSelectListener

class SearchSpiritOptFragment : Fragment() {

    companion object {

        fun newInstance(
            isEnableResearch: Boolean
        ): SearchSpiritOptFragment {
            val args = Bundle().apply {
                putBoolean("isEnableResearch", isEnableResearch)
            }

            val fragment = SearchSpiritOptFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val binding by lazy { FragmentSearchSpiritOptBinding.inflate(layoutInflater) }
    private val viewmodel by activityViewModels<SearchViewModel>()

    private val isEnableResearch by lazy {
        arguments?.getBoolean("isEnableResearch")?: false
    }

    var onResearchListener: ISearchAction? = null

    private val groupAdapter by lazy {
        SearchGroupAdapter().apply {
            onItemSelectListener = object : OnItemSelectListener<SearchGroup> {
                override fun onItemSelected(data: SearchGroup, position: Int, selected: Boolean) {
                    viewmodel.modifyGroup(if (selected) data.id.toInt() else -1)
                }
            }
        }
    }

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

        val equalDivider = ContextCompat.getDrawable(requireContext(), R.drawable.equal_divider)!!
        binding.rvGroups.adapter = groupAdapter
        binding.rvGroups.addItemDecoration(EqualDivider(equalDivider, 4))
        binding.rvProperties.adapter = propertyAdapter
        binding.rvProperties.addItemDecoration(EqualDivider(equalDivider, 4))

        binding.btnResearch.isEnabled = AppData.spiritGroups.isNotEmpty()
                && AppData.spiritProperties.isNotEmpty()
                && isEnableResearch

        binding.cbSearchByName.isChecked = AppData.SPData.fuzzyQuerySpiritByName

        binding.cbSearchByName.setOnCheckedChangeListener { buttonView, isChecked ->
            AppData.SPData.fuzzyQuerySpiritByName = isChecked
        }

        binding.btnResearch.setOnClickListener {
            onResearchListener?.onResearch(SearchDataType.Spirit)
            viewmodel.research()
        }
    }
}