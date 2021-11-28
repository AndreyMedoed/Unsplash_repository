package com.example.unsplash.screens.main.tabs.top_photo_list_fragment

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import com.example.unsplash.dataBase.adapters.DatabasePhotoAdapter
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.photo.Downloads
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.photo.Statistics
import com.example.unsplash.dataBase.Database
import com.example.unsplash.dataBase.adapters.DatabasePhotoUrlAdapter
import com.example.unsplash.dataBase.adapters.DatabaseUserAdapter
import com.example.unsplash.databinding.TopPhotoListLayoutBinding
import com.example.unsplash.utils.EmptyListException
import com.example.unsplash.utils.ItemOffsetDecoration
import com.example.unsplash.utils.isInternetAvailable
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
        setHasOptionsMenu(true)
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observe()
        initSwipe()
    }

    @ExperimentalPagingApi
    private fun observe() {
        Log.d("UnsplashLoggingPaging", "observe")
        val databasePhotoUrlAdapter = DatabasePhotoUrlAdapter()
        val databaseUserAdapter = DatabaseUserAdapter()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.postsOfPhotos(
                /** Передаем урл в качестве маркера*/
                TOP_PHOTOS_MARKER,
                NUMBER_PHOTOS_ON_PAGE
            ).map { pagingData ->
                /** Каждый экземпляр, который получаем из базы данных, нам нужно превратить в
                 * экземпляр обычного класса*/
                pagingData.map { photoDB ->
                    Photo(
                        id = photoDB.unsplashId,
                        description = photoDB.description,
                        urls = photoDB.photo_urls_id?.let {
                            databasePhotoUrlAdapter.fromDBPhotoUrlToPhotoUrl(it)
                        },
                        likes = photoDB.likes,
                        liked_by_user = photoDB.liked_by_user,
                        user = photoDB.user_id?.let {
                            databaseUserAdapter.fromDBUserToUser(it)
                        },
                        statistics = Statistics(Downloads(photoDB.total_downloads))
                    ) as PhotoAndCollection
                }
            }.collectLatest { pagingData ->
                Log.d("UnsplashLoggingPaging", "Передача данных адаптеру ${pagingData}")
                adapter.submitData(pagingData)
            }
        }
    }

    private fun initAdapter() {
        adapter = PagingPhotoAndCollectionAdapter(requireContext(),
            { photoId -> setLike(photoId) },
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
            // Only show the list if refresh succeeds.
            binding.topPhotoRecyclerViewId.isVisible = state.source.refresh is LoadState.NotLoading
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_photo_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search_icon) {
            val action =
                TopPhotoListFragmentDirections.actionTopPhotoListFragment2ToSearchFragment()
            findNavController().navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initSwipe() {
        binding.swipeRefreshLayoutId.setOnRefreshListener {
            adapter.refresh()
            binding.swipeRefreshLayoutId.isRefreshing = false
        }
    }

    private fun showAlertText(text: String) {
        binding.alertTextViewId.text = text
    }

    companion object {
        private const val NUMBER_PHOTOS_ON_PAGE = 25
        private const val TOP_PHOTOS_MARKER = "https://api.unsplash.com/photos"
    }
}