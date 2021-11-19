package com.example.unsplash.adapters

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.databinding.ItemCollectionListBinding
import com.example.unsplash.databinding.ItemPhotoListBinding

class PagingPhotoAndCollectionAdapter(
    private val context: Context,
    private val setLike: (photoId: String) -> Unit,
    private val deleteLike: (photoId: String) -> Unit,
    private val openCollectionPhotos: ((collection: Collection) -> Unit),
    private val openPhotoDetails: ((photo: Photo) -> Unit)
) : PagingDataAdapter<PhotoAndCollection, RecyclerView.ViewHolder>(PhotoDiffUtilCallback()) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("UnsplashLoggingPaging", "onBindViewHolder")

        if (item != null) {
            when {
                holder is PhotoHolder && item is Photo -> holder.bind(item)
                holder is CollectionHolder && item is Collection -> holder.bind(item)

            }
        } else {
            /** Если приходит null из БД, то обозначаем холдер как PhotoHolder и
             * даем ему заглушку в виде пустого экзенпляра класса фото. */
            (holder as PhotoHolder).bind(
                Photo(
                    "", "", null, null,
                    false, null, null
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Photo -> PHOTO_CONSTANT
            is Collection -> COLLECTION_CONSTANT
            null -> NULL_CONSTANT
            else -> {
                Log.d("UnsplashLoggingPaging", "getItem(position) = ${getItem(position)}")
                error("Некорректный элемент списка, нельзя сгенерировать ViewType")
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        Log.d("UnsplashLoggingPaging", "onCreateViewHolder")
        val photoBinding =
            ItemPhotoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val collectionBinding =
            ItemCollectionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            PHOTO_CONSTANT -> PhotoHolder(
                photoBinding,
                setLike,
                deleteLike,
                openPhotoDetails,
                context
            )
            COLLECTION_CONSTANT -> CollectionHolder(
                collectionBinding,
                openCollectionPhotos,
                context
            )
            NULL_CONSTANT -> PhotoHolder(photoBinding, { }, { }, { }, context)
            else -> error("Неверный ViewType в функции onCreateViewHolder")
        }
    }

    class PhotoDiffUtilCallback : DiffUtil.ItemCallback<PhotoAndCollection>() {
        override fun areItemsTheSame(
            oldItem: PhotoAndCollection,
            newItem: PhotoAndCollection
        ): Boolean {
            Log.d("UnsplashLoggingPaging", "areItemsTheSame")
            return when {
                oldItem is Photo && newItem is Photo -> oldItem.id == newItem.id
                oldItem is Collection && newItem is Collection -> oldItem.id == newItem.id
                else -> false
            }
        }


        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: PhotoAndCollection,
            newItem: PhotoAndCollection
        ): Boolean {
            Log.d("UnsplashLoggingPaging", "areContentsTheSame")
            return when {
                oldItem is Photo && newItem is Photo -> oldItem == newItem
                oldItem is Collection && newItem is Collection -> oldItem == newItem
                else -> false
            }
        }

    }

    class PhotoHolder(
        private var binding: ItemPhotoListBinding,
        private val setLike: (photoId: String) -> Unit,
        private val deleteLike: (photoId: String) -> Unit,
        private val openPhotoDetails: ((photo: Photo) -> Unit),
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        private var isLiked = false
        fun bind(photo: Photo) {

            binding.imageViewId.setOnClickListener {
                openPhotoDetails(photo)
            }

            binding.fullnameTextViewId.text = context.resources.getString(
                R.string.paging_adapter_full_name,
                photo.user?.first_name,
                photo.user?.last_name ?: ""
            )
            binding.usernameTextViewId.text = photo.user?.username
            binding.likeNumberTextViewId.text = photo.likes?.toString()

            if (photo.statistics?.downloads?.total != null) {
                binding.downloadsNumberId.text = context.resources.getString(
                    R.string.paging_adapter_downloads_number,
                    photo.statistics.downloads.total.toString()
                )
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

    class CollectionHolder(
        private val binding: ItemCollectionListBinding,
        private val openCollectionPhotos: ((collection: Collection) -> Unit)? = null,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(collection: Collection) {

            binding.collectionItemCardViewId.setOnClickListener {
                openCollectionPhotos?.invoke(collection)
            }

            Glide.with(itemView)
                .load(collection.cover_photo?.urls?.full)
                .into(binding.collectionImageViewId)

            Glide.with(itemView)
                .load(collection.user?.profile_image?.large)
                .placeholder(R.drawable.ic_cloud_download_24)
                .error(R.drawable.ic_error)
                .into(binding.roundedImageView)

            binding.collectionTitleTextViewId.text = collection.title
            binding.fullnameTextViewId.text = context.resources.getString(
                R.string.paging_adapter_full_name,
                collection.user?.first_name,
                collection.user?.last_name
            )
            binding.usernameTextViewId.text = collection.user?.username
            binding.photoNumberTextViewId.text = context.resources.getQuantityString(
                R.plurals.item_collection_photo_number,
                collection.total_photos ?: 0, collection.total_photos?.toString() ?: "0"
            )
        }
    }

    companion object {
        private const val PHOTO_CONSTANT = 1
        private const val COLLECTION_CONSTANT = 2
        private const val NULL_CONSTANT = 3
    }
}