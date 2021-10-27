package com.example.rickandmorty.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.rickandmorty.panding_recycler_view.CharacterAdapter
import com.example.unsplash.R
import com.example.unsplash.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.text.Typography.dagger

class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding: FragmentMainBinding by viewBinding()
    private val viewModel: MainFragmentViewModel by viewModels()

    private val adapter by lazy(LazyThreadSafetyMode.NONE) { CharacterAdapter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
    }

    private fun initList() {
        binding.topPhotoRecyclerViewId.adapter = adapter
        binding.topPhotoRecyclerViewId.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch {
            viewModel.photoPagingFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}