package com.example.unsplash.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.unsplash.R
import com.example.unsplash.data.Photo
import com.example.unsplash.data.PhotoAndCollection
import com.example.unsplash.databinding.ItemMyphotoListBinding
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class PhotoDelegateAdapter(
    private val setLike: (photoId: String) -> Unit,
    private val deleteLike: (photoId: String) -> Unit
) : AbsListItemAdapterDelegate<Photo, PhotoAndCollection, PhotoDelegateAdapter.PhotoHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup): PhotoHolder {
        val binding =
            ItemMyphotoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoHolder(binding, setLike, deleteLike)
    }

    override fun onBindViewHolder(item: Photo, holder: PhotoHolder, payloads: MutableList<Any>) {
        holder.bind(item)
    }

    override fun isForViewType(
        item: PhotoAndCollection,
        items: MutableList<PhotoAndCollection>,
        position: Int
    ): Boolean {
        return item is Photo
    }


    class PhotoHolder(
        private var binding: ItemMyphotoListBinding,
        private val setLike: (photoId: String) -> Unit,
        private val deleteLike: (photoId: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var isLiked = false

        fun bind(photo: Photo) {

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
                .load(photo.urls.full)
               /* .placeholder(R.drawable.ic_cloud_download_24)
                .error(R.drawable.ic_error)*/
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