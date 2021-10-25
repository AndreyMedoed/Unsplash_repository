package com.example.unsplash.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.PhotoAndCollection
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class PhotoAndCollectionAdapter(
    private val setLike: (photoId: String) -> Unit,
    private val deleteLike: (photoId: String) -> Unit,
    private val openCollectionPhotos: ((collection: Collection) -> Unit)? = null,
    private val openPhotoDetails: ((photo: Photo) -> Unit)? = null
): AsyncListDifferDelegationAdapter<PhotoAndCollection>(PhotoAndCollectionDiffUtilCallback()){

    init {
        delegatesManager.addDelegate(CollectionDelegateAdapter(openCollectionPhotos))
            .addDelegate(PhotoDelegateAdapter(setLike,deleteLike))
    }

    class PhotoAndCollectionDiffUtilCallback : DiffUtil.ItemCallback<PhotoAndCollection>() {
        override fun areItemsTheSame(oldItem: PhotoAndCollection, newItem: PhotoAndCollection): Boolean {
            return when {
                oldItem is Photo && newItem is Photo -> oldItem.id == newItem.id
                oldItem is Collection && newItem is Collection -> oldItem.id == newItem.id
                else -> false
            }
        }


        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: PhotoAndCollection, newItem: PhotoAndCollection): Boolean {
            return newItem == oldItem
        }

    }

}