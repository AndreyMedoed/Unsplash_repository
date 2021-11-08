package com.example.unsplash.screens.main.tabs.top_collectionList_fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.*
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.unsplash.R
import com.example.unsplash.adapters.PagingPhotoAndCollectionAdapter
import com.example.unsplash.adapters.PhotoAndCollectionAdapter
import com.example.unsplash.data.adapters.DatabaseCollectionAdapter
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.databinding.TopCollectionListLayoutBinding
import com.example.unsplash.screens.main.tabs.profile_fragment.myCollectionsFragment.MyCollectionsFragmentDirections
import com.example.unsplash.screens.main.tabs.profile_fragment.myLikesFragment.MyLikesFragmentDirections
import com.example.unsplash.screens.splash.fragmens.top_collectionList_fragment.TopCollectionListFragmentViewModel
import com.skillbox.github.utils.autoCleared
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TopCollectionListFragment : Fragment(R.layout.top_collection_list_layout) {

    private val binding: TopCollectionListLayoutBinding by viewBinding()
    private val viewModel: TopCollectionListFragmentViewModel by viewModels()
    private var collectionAdapter: PagingPhotoAndCollectionAdapter by autoCleared()


    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observeContent()
        initSwipe()
    }

    @ExperimentalPagingApi
    private fun observeContent() {
        val databaseCollectionAdapter = DatabaseCollectionAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.postsOfCollections(
                makeUrl(),
                NUMBER_PHOTOS_ON_PAGE
            ).map { pagingData ->
                val data =
                    pagingData.filter {
                        val bool =
                            databaseCollectionAdapter.fromDBCollectionToCollection(it.id) != null
                        if (!bool) {
                            Log.d("UnsplashLoggingPaging", "ИЗ БД ПРИШЕЛ NULL")
                        }
                        bool
                    }
                data.map { collectionDB ->
                    databaseCollectionAdapter.fromDBCollectionToCollection(collectionDB.id) as PhotoAndCollection
                }
            }.collectLatest { pagingData ->
                Log.d("UnsplashLoggingPaging", "Передача данных адаптеру ${pagingData}")
                collectionAdapter.submitData(pagingData)
            }
        }
    }

    private fun initAdapter() {
        collectionAdapter = PagingPhotoAndCollectionAdapter({ photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) },
            { collection -> openCollection(collection) },
            {}
        )
        binding.recyclerViewId.adapter = collectionAdapter
        binding.recyclerViewId.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        collectionAdapter.addLoadStateListener { state: CombinedLoadStates ->
            with(binding) {
                recyclerViewId.isVisible = state.refresh != LoadState.Loading
                progressBarId.isVisible = state.refresh == LoadState.Loading
            }
        }
    }

    private fun initSwipe() {
        binding.swipeRefreshLayoutId.setOnRefreshListener {
            collectionAdapter.refresh()
            binding.swipeRefreshLayoutId.isRefreshing = false
        }
    }

    private fun makeUrl(): String {
        val url = "https://api.unsplash.com/$COLLECTIONS"
        return url
    }


    private fun setLike(photoId: String) {
        viewModel.setLike(photoId)
    }

    private fun deleteLike(photoId: String) {
        viewModel.deleteLike(photoId)
    }


    private fun openCollection(collection: Collection) {
        val action =
            TopCollectionListFragmentDirections.actionTopCollectionListFragment2ToCollectionFragment2(
                collection
            )
        findNavController().navigate(action)
    }


    companion object {
        private const val PROFILE_USERNAME_KEY = "SELECTED_PROFILE_NAVIGATION_ID"
        private const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PROFILE"
        private const val NUMBER_PHOTOS_ON_PAGE = 25
        private const val COLLECTIONS = "collections"
    }
}