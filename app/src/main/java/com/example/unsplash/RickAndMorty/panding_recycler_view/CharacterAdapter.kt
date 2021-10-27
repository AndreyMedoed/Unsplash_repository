package com.example.rickandmorty.panding_recycler_view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.rickandmorty.objects.Character
import com.example.unsplash.R
import com.example.unsplash.adapters.PagingPhotoAdapter
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.databinding.ItemCharacterBinding
import com.example.unsplash.databinding.ItemPhotoListBinding

class CharacterAdapter(context: Context) :
    PagingDataAdapter<Photo, PhotoHolder>(PhotoDiffUtilCallback()) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        Log.d("UnsplashLoggingPaging", "onCreateViewHolder")
        val binding =
            ItemPhotoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoHolder(binding)
    }

}

class PhotoHolder(
    private var binding: ItemPhotoListBinding/*,
    private val setLike: (photoId: String) -> Unit,
    private val deleteLike: (photoId: String) -> Unit*/
) : RecyclerView.ViewHolder(binding.root) {
    private var isLiked = false

    fun bind(photo: Photo) {
        Log.d("UnsplashLoggingPaging", "PhotoHolder -> bind")


        binding.fullnameTextViewId.text = "${photo.user?.first_name} ${photo.user?.last_name}"
        binding.usernameTextViewId.text = photo.user?.username
        binding.likeNumberTextViewId.text = photo.likes?.toString()

        if (photo.statistics != null) {
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


//        binding.lottieLikeId.apply {
//            setMaxFrame("likeAction2")
//            setMinFrame("likeAction1")
//
//            if (photo.liked_by_user) {
//                frame = 37
//                isLiked = true
//            }
//
//            setOnClickListener { likeButton ->
//                likeButton as LottieAnimationView
//
//                isLiked = if (!isLiked) {
//                    likeButton.speed = 1.5f
//                    likeButton.playAnimation()
//                    setLike(photo.id)
//                    photo.likes = photo.likes?.plus(1)
//                    binding.likeNumberTextViewId.text = (photo.likes).toString()
//                    true
//                } else {
//                    likeButton.speed = -1.5f
//                    likeButton.playAnimation()
//                    deleteLike(photo.id)
//                    photo.likes = photo.likes?.plus(-1)
//                    binding.likeNumberTextViewId.text = (photo.likes).toString()
//                    false
//                }
//            }
//        }
    }
}

class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding: ItemCharacterBinding by viewBinding()

    fun bind(character: Character) {
        with(binding) {
            Glide.with(itemView)
                .load(character.image)
                .into(imageViewCharacterAvatar)
            textViewCharacterName.text = character.name
        }
    }
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