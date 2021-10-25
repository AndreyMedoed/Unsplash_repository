package com.example.unsplash.fragmens.top_collectionList_fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.unsplash.R
import com.example.unsplash.adapters.PhotoAndCollectionAdapter
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.databinding.TopCollectionListLayoutBinding
import com.skillbox.github.utils.autoCleared

class TopCollectionListFragment : Fragment(R.layout.top_collection_list_layout) {

    private val binding: TopCollectionListLayoutBinding by viewBinding()
    private val viewModel: TopCollectionListFragmentViewModel by viewModels()
    private var adapter: PhotoAndCollectionAdapter by autoCleared()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observe()
        getTopCollections()
    }

    private fun getTopCollections() {
        viewModel.getTopCollections()
    }

    private fun observe() {
        viewModel.listLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "My Collection list is $it")
            it?.let { adapter.items = it }
        }
    }

    private fun initAdapter() {
        adapter = PhotoAndCollectionAdapter({ photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) },
            { collection -> openCollection(collection) }
        )

        binding.topCollectionRecyclerViewId.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.topCollectionRecyclerViewId.adapter = adapter
    }

    private fun setLike(photoId: String) {
        viewModel.setLike(photoId)
    }

    private fun deleteLike(photoId: String) {
        viewModel.deleteLike(photoId)
    }

    private fun openCollection(collection: Collection) {
        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val action =
            TopCollectionListFragmentDirections.actionTopCollectionListFragmentToCollectionFragment(
                collection
            )
        findNavController().navigate(action)
    }


    companion object {
        private const val SELECTED_PROFILE_NAVIGATION_ID_KEY = "SELECTED_PROFILE_NAVIGATION_ID"
        private const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PROFILE"
    }
}