package com.lanier.roco.picturebook.feature.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.databinding.FragmentSearchOptBinding
import com.lanier.roco.picturebook.feature.search.entity.SearchGroup
import com.lanier.roco.picturebook.feature.search.entity.SearchProperty
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.widget.rv.EqualDivider
import com.lanier.roco.picturebook.widget.rv.OnItemSelectListener

class SearchOptFragment : Fragment() {

    companion object {

        fun newInstance(): SearchOptFragment {
            val args = Bundle()

            val fragment = SearchOptFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val binding by lazy { FragmentSearchOptBinding.inflate(layoutInflater) }
    private val viewmodel by activityViewModels<SearchViewModel>()

    private var currentSelectProperty: Int = -1
    private var currentSelectGroup: Int = -1

    var onResearchListener: OnResearchListener? = null

    private val groupAdapter by lazy {
        SearchGroupAdapter().apply {
            onItemSelectListener = object : OnItemSelectListener<SearchGroup> {
                override fun onItemSelected(data: SearchGroup, position: Int, selected: Boolean) {
                    currentSelectGroup = if (selected) data.id.toInt() else -1
                }
            }
        }
    }

    private val propertyAdapter by lazy {
        SearchPropertyAdapter().apply {
            onItemSelectListener = object : OnItemSelectListener<SearchProperty> {
                override fun onItemSelected(data: SearchProperty, position: Int, selected: Boolean) {
                    currentSelectProperty = if (selected) data.id.toInt() else -1
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

        binding.btnResearch.isEnabled = AppData.spiritGroups.isNotEmpty() && AppData.spiritProperties.isNotEmpty()

        binding.cbSearchByName.isChecked = AppData.SPData.fuzzyQueryByName

        binding.cbSearchByName.setOnCheckedChangeListener { buttonView, isChecked ->
            AppData.SPData.fuzzyQueryByName = isChecked
        }

        binding.btnResearch.setOnClickListener {
            onResearchListener?.onResearch()
            viewmodel.modifyProperty(currentSelectProperty)
            viewmodel.modifyGroup(currentSelectGroup)
            viewmodel.research()
        }
    }

    interface OnResearchListener {
        fun onResearch()
    }
}