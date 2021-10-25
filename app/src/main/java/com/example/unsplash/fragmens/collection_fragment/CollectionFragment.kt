package com.example.unsplash.fragmens.collection_fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.unsplash.R
import com.example.unsplash.adapters.PhotoAndCollectionAdapter
import com.example.unsplash.databinding.CollectionLayoutBinding
import com.skillbox.github.utils.autoCleared

class CollectionFragment : Fragment(R.layout.collection_layout) {

    private val binding: CollectionLayoutBinding by viewBinding()
    private var adapter: PhotoAndCollectionAdapter by autoCleared()
    private val args: CollectionFragmentArgs by navArgs()
    private val viewModel: CollectionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindCollection()
        initAdapter()
        observe()
        openCollectionPhotos()
    }

    private fun openCollectionPhotos() {
        viewModel.openCollectionPhotos(args.collection.id)
    }

    private fun observe() {
        viewModel.listLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "My profile is $it")
            it?.let { adapter.items = it }
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

        Log.d("UnsplashLoggingGlide", "My args.collection.cover_photo is ${args.collection.cover_photo}")

    }


    private fun initAdapter() {
        adapter = PhotoAndCollectionAdapter({ photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) })

        binding.recyclerViewId.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewId.adapter = adapter
    }

    private fun setLike(photoId: String) {
        viewModel.setLike(photoId)
    }

    private fun deleteLike(photoId: String) {
        viewModel.deleteLike(photoId)
    }

}