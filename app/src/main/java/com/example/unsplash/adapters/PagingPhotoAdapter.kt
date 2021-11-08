package com.example.unsplash.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.unsplash.R
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.databinding.ItemPhotoListBinding

class PagingPhotoAdapter(
    private val setLike: (photoId: String) -> Unit,
    private val deleteLike: (photoId: String) -> Unit,
    private val openPhotoDetails: ((photo: Photo) -> Unit)
) : PagingDataAdapter<Photo, PagingPhotoAdapter.PhotoHolder>(PhotoDiffUtilCallback()) {


    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val photoItem = getItem(position)
        Log.d("UnsplashLoggingPaging", "onBindViewHolder")
        if (photoItem != null) {
            holder.bind(photoItem)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoHolder {
        Log.d("UnsplashLoggingPaging", "onCreateViewHolder")
        val binding =
            ItemPhotoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoHolder(binding, setLike, deleteLike, openPhotoDetails)
    }

    class PhotoDiffUtilCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            Log.d("UnsplashLoggingPaging", "areItemsTheSame")
            return oldItem.id == newItem.id
        }


        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            Log.d("UnsplashLoggingPaging", "areContentsTheSame")
            return newItem == oldItem
        }

    }

    class PhotoHolder(
        private var binding: ItemPhotoListBinding,
        private val setLike: (photoId: String) -> Unit,
        private val deleteLike: (photoId: String) -> Unit,
        private val openPhotoDetails: ((photo: Photo) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {
        private var isLiked = false

        fun bind(photo: Photo) {

            binding.imageViewId.setOnClickListener {
                openPhotoDetails(photo)
            }

            binding.fullnameTextViewId.text = "${photo.user?.first_name} ${photo.user?.last_name}"
            binding.usernameTextViewId.text = photo.user?.username
            binding.likeNumberTextViewId.text = photo.likes?.toString()

            if (photo.statistics?.downloads?.total != null) {
                binding.downloadsNumberId.text = "(${photo.statistics?.downloads?.total})"
                binding.downloadsNumberId.isVisible = true
                binding.downIconId.isVisible = true
                binding.textButtonDownload.isVisible = true
            } else {
                binding.downloadsNumberId.isVisible = false
                binding.downIconId.isVisible = false
                binding.textButtonDownload.isVisible = false
            }

            Glide.with(itemView)
                .load(photo.urls?.full)
                .into(binding.imageViewId)

            Glide.with(itemView)
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
                        true
                    } else {
                        likeButton.speed = -1.5f
                        likeButton.playAnimation()
                        deleteLike(photo.id)
                        photo.likes = photo.likes?.plus(-1)
                        binding.likeNumberTextViewId.text = (photo.likes).toString()
                        false
                    }
                }
            }
        }
    }
}