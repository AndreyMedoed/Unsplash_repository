package com.example.unsplash.screens.main.tabs.top_photo_list_fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.unsplash.R
import com.example.unsplash.adapters.ListLoaderStateAdapter
import com.example.unsplash.adapters.PagingPhotoAndCollectionAdapter
import com.example.unsplash.data.adapters.DatabasePhotoAdapter
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.databinding.TopPhotoListLayoutBinding
import com.example.unsplash.screens.splash.fragmens.top_photo_list_fragment.TopPhotoListFragmentViewModel
import com.example.unsplash.utils.ItemOffsetDecoration
import com.skillbox.github.utils.autoCleared
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class TopPhotoListFragment : Fragment(R.layout.top_photo_list_layout) {
    private val binding: TopPhotoListLayoutBinding by viewBinding()
    private val viewModel: TopPhotoListFragmentViewModel by viewModels()
    private var adapter: PagingPhotoAndCollectionAdapter by autoCleared()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observe()
    }


    @ExperimentalPagingApi
    private fun observe() {
        Log.d("UnsplashLoggingPaging", "observe")
        val databaseAdapter = DatabasePhotoAdapter()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.postsOfPhotos(TOP_PHOTOS_MARKER, NUMBER_PHOTOS_ON_PAGE).map { pagingData ->
                val data = pagingData.filter { databaseAdapter.fromDBPhotoToPhoto(it.id) != null }
                data.map { photoDB ->
                        databaseAdapter.fromDBPhotoToPhoto(photoDB.id) as PhotoAndCollection
                }
            }.collectLatest { pagingData ->
                Log.d("UnsplashLoggingPaging", "Передача данных адаптеру ${pagingData}")
                adapter.submitData(pagingData)
            }
        }
    }

    private fun initAdapter() {
        adapter = PagingPhotoAndCollectionAdapter({ photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) },
            {},
            { photo -> openPhotoDetails(photo) })
        Log.d("UnsplashLoggingPaging", "initAdapter")

        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.topPhotoRecyclerViewId.layoutManager = staggeredGridLayoutManager
        binding.topPhotoRecyclerViewId.adapter = adapter.withLoadStateHeaderAndFooter(
            ListLoaderStateAdapter(),
            ListLoaderStateAdapter()
        )
        binding.topPhotoRecyclerViewId.addItemDecoration(
            ItemOffsetDecoration(requireContext())
        )
        binding.topPhotoRecyclerViewId.itemAnimator = FlipInTopXAnimator()

        adapter.addLoadStateListener { state: CombinedLoadStates ->
            with(binding) {
                topPhotoRecyclerViewId.isVisible = state.refresh != LoadState.Loading
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

    private fun openPhotoDetails(photo: Photo) {
        val action =
            TopPhotoListFragmentDirections.actionTopPhotoListFragment2ToPhotoDetailFragment4(photo)
        findNavController().navigate(action)
    }

    companion object {
        private const val NUMBER_PHOTOS_ON_PAGE = 25
        private const val TOP_PHOTOS_MARKER = "https://api.unsplash.com/photos"
    }
}