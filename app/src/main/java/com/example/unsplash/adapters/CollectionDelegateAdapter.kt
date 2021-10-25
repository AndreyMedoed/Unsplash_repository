package com.example.unsplash.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unsplash.R
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.databinding.ItemCollectionListBinding
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class CollectionDelegateAdapter(private val openCollectionPhotos: ((collection: Collection) -> Unit)? = null) :
    AbsListItemAdapterDelegate<Collection, PhotoAndCollection, CollectionDelegateAdapter.CollectionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): CollectionHolder {
        val binding =
            ItemCollectionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionHolder(binding, openCollectionPhotos)
    }

    override fun onBindViewHolder(
        item: Collection,
        holder: CollectionHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    override fun isForViewType(
        item: PhotoAndCollection,
        items: MutableList<PhotoAndCollection>,
        position: Int
    ): Boolean {
        return item is Collection
    }


    class CollectionHolder(
        private val binding: ItemCollectionListBinding,
        private val openCollectionPhotos: ((collection: Collection) -> Unit)? = null
    ) :
        RecyclerView.ViewHolder(binding.root) {

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
            binding.fullnameTextViewId.text =
                "${collection.user?.first_name} ${collection.user?.last_name}"
            binding.usernameTextViewId.text = collection.user?.username
            binding.photoNumberTextViewId.text = collection.total_photos.toString()
        }
    }
}