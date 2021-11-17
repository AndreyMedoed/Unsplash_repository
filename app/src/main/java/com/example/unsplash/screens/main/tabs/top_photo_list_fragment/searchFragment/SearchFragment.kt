package com.example.unsplash.screens.main.tabs.top_photo_list_fragment.searchFragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.unsplash.R
import com.example.unsplash.adapters.PagingPhotoAndCollectionAdapter
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.databinding.SearchFragmentLayoutBinding
import com.example.unsplash.utils.textChangedFlow
import com.skillbox.github.utils.autoCleared
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SearchFragment : Fragment(R.layout.search_fragment_layout) {

    private val binding: SearchFragmentLayoutBinding by viewBinding()
    private val viewModel: SearchViewModel by viewModels()
    private var photoAdapter: PagingPhotoAndCollectionAdapter by autoCleared()
    private val unsplashUrlPattern =
        Pattern.compile("^https:\\/\\/unsplash\\.com\\/photos\\/[\\w\\-_]+\$")
    private val job: Job? = null

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        bind()
    }

    override fun onResume() {
        super.onResume()
        Log.d("FlowLogging", "onResume")
    }

    private fun initAdapter() {
        photoAdapter = PagingPhotoAndCollectionAdapter({ photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) },
            { },
            { photo -> openPhotoDetail(photo) }
        )
        binding.recyclerViewId.adapter = photoAdapter
        binding.recyclerViewId.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        photoAdapter.addLoadStateListener { state: CombinedLoadStates ->
            with(binding) {
                recyclerViewId.isVisible = state.refresh != LoadState.Loading
                progressBarId.isVisible = state.refresh == LoadState.Loading
            }
        }
    }

    private fun setLike(photoId: String) {
        viewModel.setLike(photoId)
    }

    private fun deleteLike(photoId: String) {
        viewModel.deleteLike(photoId)
    }

    private fun openPhotoDetail(photo: Photo) {
        Log.d("FlowLogging", "openPhotoDetail")
        val action = SearchFragmentDirections.actionSearchFragmentToPhotoDetailFragment4(photo)
        findNavController().navigate(action)
    }

    @ExperimentalPagingApi
    private fun bind() {
        val queryFlow = binding.searchEditTextId.textChangedFlow()
        observeSearchView(queryFlow)
    }

    @ExperimentalPagingApi
    private fun observeSearchView(flow: Flow<String>) {
        viewLifecycleOwner.lifecycleScope.launch {
            val flow1 = flow.onEach {
                if (unsplashUrlPattern.matcher(it).matches()) {
                    viewModel.getPhoto(it)
                }
            }

            val flow = viewModel.searchPhoto(flow1)

            flow.collectLatest { currentFlow ->
                currentFlow.collectLatest {
                    Log.d("FlowLogging", "ПОЛУЧЕНО PAGING DATA Для текущего FLOW")
                    Log.d("FlowLogging", "ПОЛУЧЕНО PAGING DATA Для текущего FLOW $it")
                    photoAdapter.submitData(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isMatchesLiveData.observe(viewLifecycleOwner) { photo ->
                Log.d("FlowLogging", "observe isMatchesLiveData")
                binding.searchEditTextId.setText("")
                openPhotoDetail(photo)
            }
        }

    }

    companion object {
        private const val PROFILE_USERNAME_KEY = "SELECTED_PROFILE_NAVIGATION_ID"
        private const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PROFILE"
        private const val NUMBER_PHOTOS_ON_PAGE = 25
        private const val LIKED_PHOTOS_MARKER_START = "users/"
        private const val LIKED_PHOTOS_MARKER_END = "/likes"
        private const val MY_PHOTOS_MARKER_START = "users/"
        private const val MY_PHOTOS_MARKER_END = "/photos"
        private const val MY_COLLECTIONS_MARKER_START = "users/"
        private const val MY_COLLECTIONS_MARKER_END = "/collections"
    }
}