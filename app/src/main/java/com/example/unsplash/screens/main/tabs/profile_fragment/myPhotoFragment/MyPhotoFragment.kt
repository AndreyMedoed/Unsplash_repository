package com.example.unsplash.screens.main.tabs.profile_fragment.myPhotoFragment

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
import com.example.unsplash.screens.splash.fragmens.profile_fragment.myPhotoFragment.MyPhotoViewModel
import com.example.unsplash.utils.EmptyListException
import com.skillbox.github.utils.autoCleared
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MyPhotoFragment : Fragment(R.layout.photo_list_fragment_layout) {

    private val binding: PhotoListFragmentLayoutBinding by viewBinding()
    private val viewModel: MyPhotoViewModel by viewModels()
    private var myPhotoAdapter: PagingPhotoAndCollectionAdapter by autoCleared()

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
        sharedPreferences.getString(PROFILE_USERNAME_KEY, null)?.let { username ->
            observeContent(username)
        }
        initSwipe()
    }

    @ExperimentalPagingApi
    private fun observeContent(username: String) {

        val databasePhotoAdapter = DatabasePhotoAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.postsOfPhotos(
                /** ???????????????? ?????? ?? ???????????????? ??????????????*/
                makeUrl(username),
                NUMBER_PHOTOS_ON_PAGE
            ).map { pagingData ->
                /** ???????????? ??????????????????, ?????????????? ???????????????? ???? ???????? ????????????, ?????? ?????????? ???????????????????? ??
                 * ?????????????????? ???????????????? ????????????*/
                pagingData.map { photoDB ->
                    databasePhotoAdapter.fromDBPhotoToPhoto(photoDB.id) as PhotoAndCollection
                }
            }.collectLatest { pagingData ->
                Log.d("UnsplashLoggingPaging", "???????????????? ???????????? ????????????????")
                myPhotoAdapter.submitData(pagingData)
            }
        }
    }


    private fun initAdapter() {
        myPhotoAdapter = PagingPhotoAndCollectionAdapter(requireContext(),
            { photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) },
            { },
            { photo -> openPhotoDetail(photo) }
        )
        binding.recyclerViewId.adapter = myPhotoAdapter
        binding.recyclerViewId.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        myPhotoAdapter.addLoadStateListener { state: CombinedLoadStates ->
            // Only show the list if refresh succeeds.
            binding.recyclerViewId.isVisible = state.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBarId.isVisible = state.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.alertTextViewId.isVisible = state.source.refresh is LoadState.Error
            if (binding.alertTextViewId.isVisible) {
                when ((state.source.refresh as LoadState.Error).error) {
                    is EmptyListException -> showAlertText(getString(R.string.alert_text_view_empty_list))
                    else -> showAlertText(getString(R.string.alert_text_view_error))
                }
            }
        }
    }

    private fun makeUrl(username: String?): String {
        val path = MY_PHOTOS_MARKER_START + username + MY_PHOTOS_MARKER_END
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
        val action = MyPhotoFragmentDirections.actionMyPhotoFragmentToPhotoDetailFragment(photo)
        findNavController().navigate(action)
    }

    private fun initSwipe() {
//        binding.swipeRefreshLayoutId.setOnRefreshListener {
//            myPhotoAdapter.refresh()
//            binding.swipeRefreshLayoutId.isRefreshing = false
//        }
    }

    private fun showAlertText(text: String) {
        binding.alertTextViewId.text = text
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