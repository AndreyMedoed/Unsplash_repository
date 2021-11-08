package com.example.unsplash.screens.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.unsplash.screens.main.MainActivity
import com.example.unsplash.R
import com.example.unsplash.databinding.FragmentSplashBinding
import com.example.unsplash.screens.main.tabs.profile_fragment.ProfileFragment
import ua.cn.stu.navcomponent.tabs.screens.main.MainActivityArgs
import ua.cn.stu.navcomponent.tabs.screens.splash.SplashViewModel

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashBinding.bind(view)

        // just some animations example
        renderAnimations()

        viewModel.launchMainScreenEvent.observe(viewLifecycleOwner) { launchMainScreen(it) }
    }

    private fun launchMainScreen(isShown: Boolean) {
        val intent = Intent(requireContext(), MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val args = MainActivityArgs(isShown)
        intent.putExtras(args.toBundle())
        startActivity(intent)
    }

    private fun renderAnimations() {
        binding.loadingIndicator.alpha = 0f
        binding.loadingIndicator.animate()
            .alpha(0.7f)
            .setDuration(1000)
            .start()

        binding.pleaseWaitTextView.alpha = 0f
        binding.pleaseWaitTextView.animate()
            .alpha(1f)
            .setStartDelay(500)
            .setDuration(1000)
            .start()

    }
}