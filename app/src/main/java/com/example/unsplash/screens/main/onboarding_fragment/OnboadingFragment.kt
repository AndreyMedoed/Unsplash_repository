package com.example.unsplash.screens.main.onboarding_fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Scene
import androidx.transition.Slide
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.unsplash.R
import com.example.unsplash.databinding.OnboardingLayoutBinding
import com.example.unsplash.screens.splash.fragmens.onboarding_fragment.OnboardingViewModel

class OnboadingFragment : Fragment(R.layout.onboarding_layout) {
    private val binding: OnboardingLayoutBinding by viewBinding()
    private val viewModel: OnboardingViewModel by viewModels()

    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences(
            SHARED_PREFERENCE_NAME_PROFILE,
            Context.MODE_PRIVATE
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
        viewModel.nextScene()
        onBoardingIsShown()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    private fun bind() {
        val scene_anim_1 = Scene.getSceneForLayout(
            binding.animationOnBoardingLayoutId,
            R.layout.onboarding_layout_scene1,
            requireContext()
        )
        val scene_anim_2 = Scene.getSceneForLayout(
            binding.animationOnBoardingLayoutId,
            R.layout.onboarding_layout_scene2,
            requireContext()
        )
        val scene_anim_3 = Scene.getSceneForLayout(
            binding.animationOnBoardingLayoutId,
            R.layout.onboarding_layout_scene3,
            requireContext()
        )
        val scene_anim_4 = Scene.getSceneForLayout(
            binding.animationOnBoardingLayoutId,
            R.layout.onboarding_layout_scene4,
            requireContext()
        )
        val scene_oval = Scene.getSceneForLayout(
            binding.ovalOnBoardingLayoutId,
            R.layout.onboarding_layout_scene_oval,
            requireContext()
        )
        scene_anim_1.enter()


        val slide_bottom = Slide(Gravity.BOTTOM)
        slide_bottom.duration = 1300
        TransitionManager.go(scene_oval, slide_bottom)



        binding.nextSlideFabId.setOnClickListener {
            viewModel.nextScene()
        }



        viewModel.changeSceneLiveData.observe(viewLifecycleOwner) {
            val animForward = android.view.animation.AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.text_view_animation_forward
            )
            val slide_top = Slide(Gravity.TOP)
            slide_top.duration = 500

            when (it) {
                2 -> {
                    binding.onBoardingTextViewId.text =
                        "Создавайте снимки, публикуйте, собирайте аудиторию, получайте фидбек!"
                    binding.onBoardingTextViewId.startAnimation(animForward)
                    TransitionManager.go(scene_anim_2, slide_top)
                }
                3 -> {
                    binding.onBoardingTextViewId.text =
                        "Добавляйте понравившиеся фото в коллекции, и делитесь с друзьями."
                    binding.onBoardingTextViewId.startAnimation(animForward)
                    TransitionManager.go(scene_anim_3, slide_top)
                }
                4 -> {
                    binding.onBoardingTextViewId.text =
                        "Заряжайтесь новыми эмоциями с лентой лучших фото и коллекций."
                    binding.onBoardingTextViewId.startAnimation(animForward)
                    TransitionManager.go(scene_anim_4, slide_top)
                }
                else -> {
                    findNavController().navigate(OnboadingFragmentDirections.actionOnboadingFragment2ToSignInFragment())
                }
            }
        }
    }

    private fun onBoardingIsShown() {
        sharedPreferences.edit()
            .putBoolean(
                ON_BOARDING_IS_SHOWN,
                true
            )
            .apply()
        Log.d("bundleLogging", "putOnBoardingStateInSharedPref")
    }

    companion object {
        private const val ON_BOARDING_IS_SHOWN = "ON_BOARDING_IS_SHOWN"
        private const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PROFILE"
    }
}