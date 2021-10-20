package com.example.unsplash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.unsplash.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    val binding: ActivityMainBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


      //  initBottomNavigationView()

    }
/*
    private fun initBottomNavigationView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.mainBottomNavigationViewId.setupWithNavController(navController)
        binding.mainBottomNavigationViewId.selectedItemId =
            R.id.topPhotoListFragment
        binding.mainBottomNavigationViewId.isVisible = true
    }
*/

}