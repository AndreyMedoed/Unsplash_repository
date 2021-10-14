package com.example.unsplash.fragmens.profile_fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.unsplash.R
import com.example.unsplash.adapters.PhotoAndCollectionAdapter
import com.example.unsplash.data.Collection
import com.example.unsplash.data.Profile
import com.example.unsplash.data.User
import com.example.unsplash.databinding.ProfileLayoutBinding
import com.skillbox.github.utils.autoCleared

class ProfileFragment : Fragment(R.layout.profile_layout) {

    private val binding: ProfileLayoutBinding by viewBinding()
    private val viewModel: ProfileViewModel by viewModels()
    private var adapter: PhotoAndCollectionAdapter by autoCleared()

    init {
        arguments = Bundle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomNavigationView()
        observe()
        getMyProfile()
        if (arguments?.getInt(SELECTED_PROFILE_NAVIGATION_ID_KEY) != null) {
            binding.profileBottomNavigationId.selectedItemId = arguments?.getInt(SELECTED_PROFILE_NAVIGATION_ID_KEY) ?: R.id.collection_list_item
            binding.profileBottomNavigationId.performClick()
        } else viewModel.getMyPhotos()
    }

    private fun getMyProfile() {
        viewModel.getMyProfile()
    }


    private fun observe() {
        viewModel.profileLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "My profile is $it")
            bindProfile(it)
        }
        viewModel.userLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "My profile is $it")
            bindProfilePhoto(it)
        }
        viewModel.listLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "My profile is $it")
            it?.let { adapter.items = it }
        }
    }

    private fun bindProfile(profile: Profile?) {
        if (profile == null) return
        binding.bioId.text = profile.bio
        binding.emailId.text = profile.email
        binding.firstLastNameId.text = "${profile.first_name} ${profile.last_name}"
        binding.locationId.text = profile.location
        binding.usernameId.text = profile.username
        binding.downloadsId.text = profile.downloads.toString()
    }

    private fun bindProfilePhoto(user: User?) {
        if (user == null) return
        binding.profileAvatar
        Glide.with(requireContext())
            .load(user.profile_image?.large)
            .into(binding.profileAvatar)

    }


    private fun initBottomNavigationView() {
        adapter = PhotoAndCollectionAdapter({ photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) }
        ) { collection -> openCollection(collection) }

        binding.recyclerViewId.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewId.adapter = adapter

        binding.profileBottomNavigationId.setOnItemSelectedListener { item ->
            if (!item.isChecked) {
                when (item.itemId) {
                    R.id.photo_list_item -> {
                        viewModel.getMyPhotos()
                        true
                    }
                    R.id.likes_list_item -> {
                        viewModel.getMyLikesList()
                        true
                    }
                    R.id.collection_list_item -> {
                        viewModel.getMyCollectionList()
                        true
                    }
                    else -> false
                }
            } else false

        }
        binding.profileBottomNavigationId.selectedItemId = R.id.photo_list_item
    }

    private fun setLike(photoId: String) {
        viewModel.setLike(photoId)
    }

    private fun deleteLike(photoId: String) {
        viewModel.deleteLike(photoId)
    }

    private fun openCollection(collection: Collection) {
        val action = ProfileFragmentDirections.actionProfileFragmentToCollectionFragment(collection)
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        arguments?.putInt(
            SELECTED_PROFILE_NAVIGATION_ID_KEY,
            binding.profileBottomNavigationId.selectedItemId
        )
        Log.d("bundleLogging", "arguments?.putInt")
    }

    companion object {
        private const val SELECTED_PROFILE_NAVIGATION_ID_KEY = "SELECTED_PROFILE_NAVIGATION_ID"
    }
}