package com.example.unsplash.screens.main.collection_fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.unsplash.R
import com.example.unsplash.adapters.PagingPhotoAndCollectionAdapter
import com.example.unsplash.dataBase.adapters.DatabasePhotoAdapter
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.databinding.CollectionMyLayoutBinding
import com.skillbox.github.utils.autoCleared
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** Этот фрагмент для коллекций которые открываются внутри  MyCollectionsFragment, в профиле.*/
class CollectionFragmentInMy : Fragment(R.layout.collection_my_layout) {

    private val binding: CollectionMyLayoutBinding by viewBinding()
    private var photoAdapter: PagingPhotoAndCollectionAdapter by autoCleared()
    private val args: CollectionFragmentArgs by navArgs()
    private val viewModel: CollectionViewModel by viewModels()

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindCollection()
        initAdapter()
        args.collection.links?.photos?.let { photoUrl ->
            observeContent(photoUrl)
        }
    }

    @ExperimentalPagingApi
    private fun observeContent(photoUrl: String) {

        val databasePhotoAdapter = DatabasePhotoAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.postsOfPhotos(
                /** Передаем урл в качестве маркера*/
                photoUrl,
                NUMBER_PHOTOS_ON_PAGE
            ).map { pagingData ->
                /** Каждый экземпляр, который получаем из базы данных, нам нужно превратить в
                 * экземпляр обычного класса*/
                pagingData.map { photoDB ->
                    databasePhotoAdapter.fromDBPhotoToPhoto(photoDB.id) as PhotoAndCollection
                }
            }.collectLatest { pagingData ->
                Log.d("UnsplashLoggingPaging", "Передача данных адаптеру")
                photoAdapter.submitData(pagingData)
            }
        }
    }


    private fun initAdapter() {
        photoAdapter = PagingPhotoAndCollectionAdapter(requireContext(),
            { photoId -> setLike(photoId) },
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

    private fun bindCollection() {

        binding.collectionTitleId.text = args.collection.title
        binding.descriptionId.text = args.collection.description
        binding.numberOfPhotoTextViewId.text = args.collection.total_photos.toString()
        binding.usernameTextViewId.text = args.collection.user?.username

        Glide.with(this)
            .load(args.collection.cover_photo?.urls?.full)
            .error(R.drawable.ic_error)
            .into(binding.coverImageViewId)

        Log.d(
            "UnsplashLoggingGlide",
            "My args.collection.cover_photo is ${args.collection.cover_photo}"
        )

    }

    private fun setLike(photoId: String) {
        viewModel.setLike(photoId)
    }

    private fun deleteLike(photoId: String) {
        viewModel.deleteLike(photoId)
    }


    private fun openPhotoDetail(photo: Photo) {
        val action = CollectionFragmentInMyDirections.actionCollectionFragmentToPhotoDetailFragment5(photo)
        findNavController().navigate(action)
    }

    companion object {
        private const val NUMBER_PHOTOS_ON_PAGE = 25
    }
}