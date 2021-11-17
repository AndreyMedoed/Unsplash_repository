package com.example.unsplash.screens.main.photo_detail_fragment

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.unsplash.Network.NetworkBroadcastReceiver
import com.example.unsplash.R
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.photo.photo_detail.PhotoDetail
import com.example.unsplash.databinding.PhotoDetailLayoutBinding
import com.example.unsplash.screens.main.MainActivity
import com.example.unsplash.screens.main.tabs.top_photo_list_fragment.TopPhotoListFragmentDirections
import java.util.regex.Pattern

class PhotoDetailFragment : Fragment(R.layout.photo_detail_layout) {

    private val binding: PhotoDetailLayoutBinding by viewBinding()
    private val viewModel: PhotoDetailViewModel by viewModels()
    private val args: PhotoDetailFragmentArgs by navArgs()
    private val receiver = NetworkBroadcastReceiver()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    /* Лаунчер для пикера файлов, чтоб выбрать папку, куда загрузится фото*/
    private lateinit var selectDocumentLauncher: ActivityResultLauncher<Uri>
    private var isLiked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPermissionResultListener()
        initSelectDocumentFolderLauncher()
        setHasOptionsMenu(true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindPhoto(args.photo)
        observe()
        getPhotoDetail()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.photo_detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_item_id) {
            share()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun share() {
        val shareIntent = Intent().apply {
            this.action = Intent.ACTION_SEND
            this.putExtra(Intent.EXTRA_TEXT, args.photo.urls?.raw)
            this.type = "text/plain"
        }
        startActivity(shareIntent)
    }


    private fun getPhotoDetail() {
        viewModel.getPhotoDetail(args.photo.id)
    }

    private fun observe() {
        viewModel.photoDetailLiveData.observe(viewLifecycleOwner) {
            Log.d("UnsplashLogging", "PhotoDetail  is $it")
            bindPhotoDetail(it)
        }
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindPhoto(photo: Photo) {

        binding.fullnameTextViewId.text = "${photo.user?.first_name} ${photo.user?.last_name ?: ""}"
        binding.usernameTextViewId.text = photo.user?.username
        binding.usernameTextView2Id.text = "${photo.user?.username}:"
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
                "${photoDetail.location?.city ?: "-"}, ${photoDetail.location?.country ?: "-"}"
        } else binding.locationImageViewId.isVisible = false

        binding.tagsTextViewId.text = photoDetail.tags?.joinToString { "#${it.title} " }
        binding.madeWithTextViewId.text = photoDetail.exif?.make ?: "-"
        binding.modelTextViewId.text = photoDetail.exif?.model ?: "-"
        binding.exposureTextViewId.text = photoDetail.exif?.exposure_time ?: "-"
        binding.apertureTextViewId.text = photoDetail.exif?.aperture ?: "-"
        binding.focalLengthTextViewId.text = photoDetail.exif?.focal_length ?: "-"
        binding.isoTextViewId.text = photoDetail.exif?.iso?.toString() ?: "-"
        binding.descriptionTextViewId.text = photoDetail.description ?: "-"
        binding.downloadsNumberId.text = "(${photoDetail.downloads?.toString()})"
        binding.locationTextViewId.setOnClickListener {
            goToMap(
                photoDetail.location?.position?.latitude,
                photoDetail.location?.position?.longitude
            )
        }
        binding.textButtonDownload.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                downloadWithPermission()
            } else downloadWithoutPermission()
        }
    }

    private fun downloadWithPermission() {
        if (hasPermission().not()) {
            requestPermission()
        } else {
            selectDirectory()
        }
    }

    private fun downloadWithoutPermission() {
        selectDirectory()
    }

    private fun goToMap(latitude: Double?, longitude: Double?) {
        if (latitude == null || longitude == null) return
        val uri = Uri.parse("geo: $latitude $longitude")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun setLike(photoId: String) {
        viewModel.setLike(photoId)
    }

    private fun deleteLike(photoId: String) {
        viewModel.deleteLike(photoId)
    }

    /* Запрашиваем разрешения на запись и чтение файлов */
    private fun requestPermission() {
        requestPermissionLauncher.launch(*PERMISSIONS.toTypedArray())
    }

    private fun selectDirectory() {
        selectDocumentLauncher.launch(null)
    }

    private fun initPermissionResultListener() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { permissionToGrantedMap: Map<String, Boolean> ->
                if (permissionToGrantedMap.values.all { it }) {
                    selectDirectory()
                }
            }
    }


    private fun initSelectDocumentFolderLauncher() {
        selectDocumentLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri: Uri? ->
            handleSelectDirectory(uri)
        }
    }

    /* Когда выбрали папку для сохранения фото - передаем в функцию из вью модели
    * название, урл (которые я сохраняю в переменные в функции , чтоб потом здесь
    * их получить) а так же, ури выбранной папки */
    private fun handleSelectDirectory(uri: Uri?) {
        if (uri == null) {
            Toast.makeText(requireContext(), "Директория не выбрана", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            args.photo.urls?.raw?.let { raw ->
                viewModel.savePhoto(args.photo.id, raw, uri)
            }
        } catch (t: Throwable) {
            Toast.makeText(requireContext(), "Сохранить не удалось", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasPermission(): Boolean {
        Log.d("StorageScopeLogging", "hasPermission ")
        return PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(
            receiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(receiver)
    }


    companion object {
        private val PERMISSIONS = listOfNotNull(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                .takeIf { Build.VERSION.SDK_INT < Build.VERSION_CODES.Q }
        )
    }
}