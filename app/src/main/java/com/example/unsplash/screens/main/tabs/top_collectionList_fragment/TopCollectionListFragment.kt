package com.example.unsplash.screens.main.tabs.top_collectionList_fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.example.unsplash.dataBase.adapters.DatabaseCollectionAdapter
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.databinding.TopCollectionListLayoutBinding
import com.example.unsplash.utils.EmptyListException
import com.example.unsplash.utils.isInternetAvailable
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
                /** Передаем урл в качестве маркера*/
                makeUrl(),
                NUMBER_PHOTOS_ON_PAGE
            ).map { pagingData ->
                /** Каждый экземпляр, который получаем из базы данных, нам нужно превратить в
                 * экземпляр обычного класса*/
                pagingData.map { collectionDB ->
                    databaseCollectionAdapter.fromDBCollectionToCollection(collectionDB.id) as PhotoAndCollection
                }
            }.collectLatest { pagingData ->
                Log.d("UnsplashLoggingPaging", "Передача данных адаптеру ${pagingData}")
                collectionAdapter.submitData(pagingData)
            }
        }
    }

    private fun initAdapter() {
        collectionAdapter = PagingPhotoAndCollectionAdapter(requireContext(),
            { photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) },
            { collection -> openCollection(collection) },
            {}
        )
        binding.recyclerViewId.adapter = collectionAdapter
        binding.recyclerViewId.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        collectionAdapter.addLoadStateListener { state: CombinedLoadStates ->
            // Only show the list if refresh succeeds.
            binding.recyclerViewId.isVisible = state.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBarId.isVisible = state.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.alertTextViewId.isVisible = state.source.refresh is LoadState.Error
            if (binding.alertTextViewId.isVisible) {
                Log.d("UnsplashLoggingPagingEx", "LoadState is Error")
                when ((state.source.refresh as LoadState.Error).error) {
                    is EmptyListException -> showAlertText(getString(R.string.alert_text_view_empty_list))
                    else -> showAlertText(getString(R.string.alert_text_view_error))
                }
            }
            if (!isInternetAvailable(requireContext())) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_no_internet_show_database),
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun showAlertText(text: String) {
        binding.alertTextViewId.text = text
    }


    companion object {
        private const val PROFILE_USERNAME_KEY = "SELECTED_PROFILE_NAVIGATION_ID"
        private const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PROFILE"
        private const val NUMBER_PHOTOS_ON_PAGE = 25
        private const val COLLECTIONS = "collections"
    }
}