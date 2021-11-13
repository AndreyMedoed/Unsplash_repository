package com.example.unsplash.screens.main.tabs.profile_fragment.myLikesFragment

import android.content.Context
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
import com.example.unsplash.dataBase.adapters.DatabasePhotoAdapter
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.databinding.PhotoListFragmentLayoutBinding
import com.example.unsplash.screens.splash.fragmens.profile_fragment.myLikesFragment.MyLikesViewModel
import com.skillbox.github.utils.autoCleared
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MyLikesFragment : Fragment(R.layout.photo_list_fragment_layout) {

    private val binding: PhotoListFragmentLayoutBinding by viewBinding()
    private val viewModel: MyLikesViewModel by viewModels()
    private var likedPhotoAdapter: PagingPhotoAndCollectionAdapter by autoCleared()

    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences(
            SHARED_PREFERENCE_NAME_PROFILE,
            Context.MODE_PRIVATE
        )
    }


    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initSwipe()
        sharedPreferences.getString(PROFILE_USERNAME_KEY, null)?.let { username ->
            observeContent(username)
        }
    }


    @ExperimentalPagingApi
    private fun observeContent(username: String?) {

        val databasePhotoAdapter = DatabasePhotoAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.postsOfPhotos(
                /** Передаем урл в качестве маркера*/
                makeUrl(username),
                NUMBER_PHOTOS_ON_PAGE
            ).map { pagingData ->
                /** Каждый экземпляр, который получаем из базы данных, нам нужно превратить в
                 * экземпляр обычного класса*/
                pagingData.map { photoDB ->
                    Log.d("UnsplashLoggingPagingMy", "photoDB $photoDB")
                    databasePhotoAdapter.fromDBPhotoToPhoto(photoDB.id) as PhotoAndCollection
                }
            }.collectLatest { pagingData ->
                Log.d("UnsplashLoggingPaging", "Передача данных адаптеру ${pagingData}")
                likedPhotoAdapter.submitData(pagingData)
            }
        }
    }

    private fun initAdapter() {
        likedPhotoAdapter = PagingPhotoAndCollectionAdapter({ photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) },
            { },
            { photo -> openPhotoDetail(photo) }
        )
        binding.recyclerViewId.adapter = likedPhotoAdapter
        binding.recyclerViewId.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        likedPhotoAdapter.addLoadStateListener { state: CombinedLoadStates ->
            with(binding) {
                recyclerViewId.isVisible = state.refresh != LoadState.Loading
                progressBarId.isVisible = state.refresh == LoadState.Loading
            }
        }
    }


    private fun makeUrl(username: String?): String {
        val path = LIKED_PHOTOS_MARKER_START + username + LIKED_PHOTOS_MARKER_END
        val url = "https://api.unsplash.com/$path"
        return url
    }

    private fun setLike(photoId: String) {
        viewModel.setLike(photoId)
    }

    private fun deleteLike(photoId: String) {
        viewModel.deleteLike(photoId)
    }


    private fun openPhotoDetail(photo: Photo) {
        val action = MyLikesFragmentDirections.actionMyLikesFragmentToPhotoDetailFragment2(photo)
        findNavController().navigate(action)
    }

    private fun initSwipe() {
//        binding.swipeRefreshLayoutId.setOnRefreshListener {
//            likedPhotoAdapter.refresh()
//            binding.swipeRefreshLayoutId.isRefreshing = false
//        }
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