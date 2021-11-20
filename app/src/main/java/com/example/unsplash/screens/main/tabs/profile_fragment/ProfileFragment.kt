package com.example.unsplash.screens.main.tabs.profile_fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.NavigationUI
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.unsplash.R
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import com.example.unsplash.databinding.ProfileLayoutBinding
import com.example.unsplash.utils.findTopNavController

class ProfileFragment : Fragment(R.layout.profile_layout) {

    private val binding: ProfileLayoutBinding by viewBinding()
    private val viewModel: ProfileViewModel by viewModels()

    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences(
            SHARED_PREFERENCE_NAME_PROFILE,
            Context.MODE_PRIVATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        getMyProfile()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.exit_item_id) {
            showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.profile_dialog_title))
            .setMessage(getString(R.string.profile_dialog_message))
            .setNegativeButton(getString(R.string.profile_dialog_negative)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.profile_dialog_positive)) { _, _ -> exit() }
            .show()
    }

    private fun exit() {
        viewModel.clearAllDatabase()
        findTopNavController().navigate(R.id.signInFragment, null, navOptions {
            popUpTo(R.id.tabsFragment) {
                inclusive = true
            }
        })
    }

    private fun initBottomNavigationView() {
        val navHost =
            childFragmentManager.findFragmentById(R.id.profile_Tabs_Fragment_Container) as NavHostFragment
        val navController = navHost.navController
        NavigationUI.setupWithNavController(binding.profileBottomNavigationId, navController)
    }

    private fun getMyProfile() {
        viewModel.getMyProfile()
    }

    private fun observe() {
        viewModel.profileLiveData.observe(viewLifecycleOwner) { profile ->
            Log.d("UnsplashLogging", "My profile is $profile")
            bindProfile(profile)
            putUsernameInSharedPref(profile?.username)
            initBottomNavigationView()
            binding.profileBottomNavigationId.selectedItemId = R.id.my_likes
            binding.profileBottomNavigationId.performClick()
        }
        viewModel.userLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "My User is $it")
            bindProfilePhoto(it)
        }
    }


    private fun bindProfile(profile: Profile?) {
        if (profile == null) return
        binding.bioId.text = profile.bio
        binding.emailId.text = profile.email
        binding.firstLastNameId.text = resources.getString(
            R.string.profile_firstLastName,
            profile.first_name,
            profile.last_name
        )
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


    private fun putUsernameInSharedPref(username: String?) {
        sharedPreferences.edit()
            .putString(
                PROFILE_USERNAME_KEY,
                username
            )
            .apply()
        Log.d("bundleLogging", "putStateInSharedPref")
    }


    companion object {
        private const val PROFILE_USERNAME_KEY = "SELECTED_PROFILE_NAVIGATION_ID"
        const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PROFILE"
        const val PROFILE_ID_KEY = "PROFILE_ID_KEY"
        const val PROFILE_USER_ID_KEY = "PROFILE_ID_KEY"
        const val PROFILE_MY_USER_MARK = "PROFILE_MY_USER_MARK"
        private const val NUMBER_PHOTOS_ON_PAGE = 25
        private const val LIKED_PHOTOS_MARKER_START = "users/"
        private const val LIKED_PHOTOS_MARKER_END = "/likes"
        private const val MY_PHOTOS_MARKER_START = "users/"
        private const val MY_PHOTOS_MARKER_END = "/photos"
        private const val MY_COLLECTIONS_MARKER_START = "users/"
        private const val MY_COLLECTIONS_MARKER_END = "/collections"
    }
}