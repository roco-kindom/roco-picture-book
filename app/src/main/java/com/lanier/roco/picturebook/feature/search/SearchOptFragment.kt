package com.lanier.roco.picturebook.feature.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lanier.roco.picturebook.databinding.FragmentSearchOptBinding

class SearchOptFragment : Fragment() {

    private val binding by lazy { FragmentSearchOptBinding.inflate(layoutInflater) }
    private val viewmodel by activityViewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnResearch.setOnClickListener { viewmodel.research() }
    }
}