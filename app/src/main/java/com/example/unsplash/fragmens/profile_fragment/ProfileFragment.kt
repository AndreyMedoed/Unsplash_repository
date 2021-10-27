package com.example.unsplash.fragmens.profile_fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.unsplash.R
import com.example.unsplash.adapters.PhotoAndCollectionAdapter
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import com.example.unsplash.databinding.ProfileLayoutBinding
import com.skillbox.github.utils.autoCleared

class ProfileFragment : Fragment(R.layout.profile_layout) {

    private val binding: ProfileLayoutBinding by viewBinding()
    private val viewModel: ProfileViewModel by viewModels()
    private var adapter: PhotoAndCollectionAdapter by autoCleared()
    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences(
            SHARED_PREFERENCE_NAME_PROFILE,
            Context.MODE_PRIVATE
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomNavigationView()
        observe()
        getMyProfile()
    }

    private fun getMyProfile() {
        viewModel.getMyProfile()
    }


    private fun observe() {
        viewModel.profileLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "My profile is $it")
            bindProfile(it)
            setOldNavigationItem()
        }
        viewModel.userLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "My User is $it")
            bindProfilePhoto(it)
        }
        viewModel.listLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "My List Photo is $it")
            it?.let { adapter.items = it }
        }
    }

    private fun setOldNavigationItem() {
        if (sharedPreferences.getInt(SELECTED_PROFILE_NAVIGATION_ID_KEY, 100) == 100) {
            binding.profileBottomNavigationId.selectedItemId = R.id.photo_list_item
            viewModel.getMyPhotos()
            Log.d("bundleLogging", "selectedItemId == null")

        } else {
            binding.profileBottomNavigationId.selectedItemId =
                sharedPreferences.getInt(SELECTED_PROFILE_NAVIGATION_ID_KEY, 0)
            binding.profileBottomNavigationId.performClick()
            Log.d("bundleLogging", "selectedItemId!= null")
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

    private fun initAdapter() {
        adapter = PhotoAndCollectionAdapter({ photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) },
            { collection -> openCollection(collection) },
            { photo -> openPhotoDetail(photo) }
        )

        binding.recyclerViewId.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewId.adapter = adapter
    }

    private fun initBottomNavigationView() {

        initAdapter()

        binding.profileBottomNavigationId.setOnItemSelectedListener { item ->
            if (!item.isChecked) {
                when (item.itemId) {
                    R.id.photo_list_item -> {
                        viewModel.getMyPhotos()
                        putStateInSharedPref(item.itemId)
                        true
                    }
                    R.id.likes_list_item -> {
                        viewModel.getMyLikesList()
                        putStateInSharedPref(item.itemId)
                        true
                    }
                    R.id.collection_list_item -> {
                        viewModel.getMyCollectionList()
                        putStateInSharedPref(item.itemId)
                        true
                    }
                    else -> false
                }
            } else false
        }
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
        val action = ProfileFragmentDirections.actionProfileFragmentToCollectionFragment(collection)
        findNavController().navigate(action)
    }

    private fun openPhotoDetail(photo: Photo) {
        val action = ProfileFragmentDirections.actionProfileFragmentToPhotoDetailFragment(photo)
        findNavController().navigate(action)
    }

    private fun putStateInSharedPref(selectedItemId: Int) {
        sharedPreferences.edit()
            .putInt(
                SELECTED_PROFILE_NAVIGATION_ID_KEY,
                selectedItemId
            )
            .apply()
        Log.d("bundleLogging", "putStateInSharedPref")
    }


    companion object {
        private const val SELECTED_PROFILE_NAVIGATION_ID_KEY = "SELECTED_PROFILE_NAVIGATION_ID"
        private const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PROFILE"
    }
}