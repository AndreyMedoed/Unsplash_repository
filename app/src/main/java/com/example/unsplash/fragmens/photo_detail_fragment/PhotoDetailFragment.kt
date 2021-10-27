package com.example.unsplash.fragmens.photo_detail_fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.unsplash.R
import com.example.unsplash.adapters.PhotoAndCollectionAdapter
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.photo.photo_detail.PhotoDetail
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import com.example.unsplash.databinding.PhotoDetailLayoutBinding
import com.example.unsplash.databinding.ProfileLayoutBinding
import com.example.unsplash.fragmens.profile_fragment.ProfileFragmentDirections
import com.example.unsplash.fragmens.profile_fragment.ProfileViewModel
import com.skillbox.github.utils.autoCleared

class PhotoDetailFragment : Fragment(R.layout.photo_detail_layout) {

    private val binding: PhotoDetailLayoutBinding by viewBinding()
    private val viewModel: PhotoDetailViewModel by viewModels()
    private val args: PhotoDetailFragmentArgs by navArgs()
    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences(
            SHARED_PREFERENCE_NAME_PROFILE,
            Context.MODE_PRIVATE
        )
    }
    private var isLiked = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindPhoto(args.photo)
        observe()
        getPhotoDetail()
    }

    private fun getPhotoDetail() {
        viewModel.getPhotoDetail(args.photo.id)
    }


    private fun observe() {
        viewModel.photoDetailLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "PhotoDetail  is $it")
            bindPhotoDetail(it)
        }
    }


    private fun bindPhoto(photo: Photo) {

        binding.fullnameTextViewId.text = "${photo.user?.first_name} ${photo.user?.last_name}"
        binding.usernameTextViewId.text = photo.user?.username
        binding.likeNumberTextViewId.text = photo.likes?.toString()

        Glide.with(requireContext())
            .load(photo.urls?.full)
            .into(binding.imageViewId)

        Glide.with(requireContext())
            .load(photo.user?.profile_image?.large)
            .placeholder(R.drawable.ic_cloud_download_24)
            .error(R.drawable.ic_error)
            .into(binding.roundedImageView)


        binding.lottieLikeId.apply {
            setMaxFrame("likeAction2")
            setMinFrame("likeAction1")

            if (photo.liked_by_user) {
                frame = 37
                isLiked = true
            }

            setOnClickListener { likeButton ->
                likeButton as LottieAnimationView

                isLiked = if (!isLiked) {
                    likeButton.speed = 1.5f
                    likeButton.playAnimation()
                    setLike(photo.id)
                    photo.likes = photo.likes?.plus(1)
                    binding.likeNumberTextViewId.text = (photo.likes).toString()

                    setLike(photo.id)
                    true
                } else {
                    likeButton.speed = -1.5f
                    likeButton.playAnimation()
                    deleteLike(photo.id)
                    photo.likes = photo.likes?.plus(-1)
                    binding.likeNumberTextViewId.text = (photo.likes).toString()

                    deleteLike(photo.id)
                    false
                }
            }
        }
    }

    private fun bindPhotoDetail(photoDetail: PhotoDetail?) {
        if (photoDetail == null) return
        if (photoDetail.location != null) {
            binding.locationTextViewId.text =
                "${photoDetail.location?.city}, ${photoDetail.location?.country}"
        } else binding.locationImageViewId.isVisible = false

        binding.tagsTextViewId.text = photoDetail.tags?.joinToString { "#${it.title} " }
        binding.madeWithTextViewId.text = photoDetail.exif?.make
        binding.modelTextViewId.text = photoDetail.exif?.model
        binding.exposureTextViewId.text = photoDetail.exif?.exposure_time
        binding.apertureTextViewId.text = photoDetail.exif?.aperture
        binding.focalLengthTextViewId.text = photoDetail.exif?.focal_length
        binding.isoTextViewId.text = photoDetail.exif?.iso.toString()
        binding.usernameTextViewId.text = photoDetail.user?.username
        binding.descriptionTextViewId.text = photoDetail.description
        binding.downloadsNumberId.text = "(${photoDetail.downloads?.toString()})"
    }


    private fun setLike(photoId: String) {
        viewModel.setLike(photoId)
    }

    private fun deleteLike(photoId: String) {
        viewModel.deleteLike(photoId)
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
        private const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PHOTO_DETAIL"
    }
}