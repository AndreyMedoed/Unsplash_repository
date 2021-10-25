package com.example.unsplash.fragmens.top_photo_list_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.unsplash.R
import com.example.unsplash.adapters.PagingPhotoAdapter
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.databinding.TopPhotoListLayoutBinding
import com.example.unsplash.utils.ItemOffsetDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skillbox.github.utils.autoCleared
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

class TopPhotoListFragment : Fragment(R.layout.top_photo_list_layout) {
    private val binding: TopPhotoListLayoutBinding by viewBinding()
    private val viewModel: TopPhotoListFragmentViewModel by viewModels()

    //    private var adapter: PhotoAndCollectionAdapter by autoCleared()
    private var adapter: PagingPhotoAdapter by autoCleared()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.top_photo_list_layout, container, false)
        initBottomNavigationView()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observe()
        // getTopPhotos()
    }

    private fun getTopPhotos() {
        viewModel.getTopPhotos()
    }

    private fun observe() {
//        viewModel.listLiveData.observe(viewLifecycleOwner) {
//            Log.d("UnsplashLogging", "My photos list is $it")
//            it?.let { adapter.submitData()}
//        }
        Log.d("UnsplashLoggingPaging", "observe")
        viewLifecycleOwner.lifecycleScope.launchWhenCreated{
            viewModel.getTopPhotosPaging().collectLatest {
                Log.d("UnsplashLoggingPaging", "Передача данных адаптеру ${it.toString()}")
                adapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            //Your adapter's loadStateFlow here
            adapter.loadStateFlow.
            distinctUntilChangedBy {
                it.refresh
            }.collect {
                //you get all the data here
                val list = adapter.snapshot()
                Log.d("UnsplashLoggingPaging", "$list")
            }
        }
    }

    private fun initAdapter() {
//        adapter = PhotoAndCollectionAdapter({ photoId -> setLike(photoId) },
//            { photoId -> deleteLike(photoId) }, null, { photo -> openPhotoDetails(photo) })

        adapter = PagingPhotoAdapter({ photoId -> setLike(photoId) },
            { photoId -> deleteLike(photoId) })
        Log.d("UnsplashLoggingPaging", "initAdapter")

        val staggeredGridLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        //  StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.topPhotoRecyclerViewId.apply {
            layoutManager = staggeredGridLayoutManager
            adapter = adapter
            addItemDecoration(
                ItemOffsetDecoration(requireContext())
            )
            itemAnimator = FlipInTopXAnimator()
        }

//        val scrollListener =
//            object : EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
//                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
//                    loadNextDataFromApi()
//                }
//            }
//
//
//        binding.topPhotoRecyclerViewId.addOnScrollListener(scrollListener)

    }

    fun loadNextDataFromApi() {
//        if (offset < 20){creatures = (creatures + creatures[(0..7).random()])
//            creaturesAdapter?.updateCreatures(creatures)}
    }

    private fun setLike(photoId: String) {
        viewModel.setLike(photoId)
    }

    private fun deleteLike(photoId: String) {
        viewModel.deleteLike(photoId)
    }

    private fun openPhotoDetails(photo: Photo) {
        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val action =
            TopPhotoListFragmentDirections.actionTopPhotoListFragmentToPhotoDetailFragment(photo)
        findNavController().navigate(action)
    }

    private fun initBottomNavigationView() {
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.main_BottomNavigationView_Id)
        bottomNavigationView?.isVisible = true

        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView?.setupWithNavController(navController)

    }
}