package com.example.unsplash.fragmens.profile_fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.unsplash.R
import com.example.unsplash.databinding.ProfileLayoutBinding

class ProfileFragment : Fragment(R.layout.profile_layout) {

    private val binding: ProfileLayoutBinding by viewBinding()
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        getMyProfile()
    }

    private fun getMyProfile() {
        viewModel.getMyProfile()
    }

    private fun observe() {
        viewModel.profileLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "My profile is $it")
        }
    }
}