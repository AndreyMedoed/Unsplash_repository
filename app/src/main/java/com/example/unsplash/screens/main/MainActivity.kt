package com.example.unsplash.screens.main

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.unsplash.BuildConfig
import com.example.unsplash.Network.SnackBarLocalBroadcastReceiver
import com.example.unsplash.R
import com.example.unsplash.databinding.ActivityMainBinding
import com.example.unsplash.screens.main.tabs.TabsFragment
import com.google.android.material.snackbar.Snackbar
import ua.cn.stu.navcomponent.tabs.screens.main.MainActivityArgs
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {


    val binding: ActivityMainBinding by viewBinding()
    private var navController: NavController? = null
    private val topLevelDestinations = setOf(getTabsDestination(), getSignInDestination())
    private val reciever = SnackBarLocalBroadcastReceiver { uri -> showSnackBar(uri) }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>


    // fragment listener is sued for tracking current nav controller
    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is TabsFragment || f is NavHostFragment) return
            onNavControllerActivated(f.findNavController())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // preparing root nav controller
        val navController = getRootNavController()
        prepareRootNavController(isShown(), isTokenNotOutdated(), navController)
        onNavControllerActivated(navController)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)

        initPermissionResultListener()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(reciever, IntentFilter(INTENT_FILTER_SNACKBAR))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciever)

    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        navController = null
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (isStartDestination(navController?.currentDestination)) {
            super.onBackPressed()
        } else {
            navController?.popBackStack()
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        (navController?.navigateUp() ?: false) || super.onSupportNavigateUp()

    private fun prepareRootNavController(
        isShown: Boolean,
        isTokenNotOutdated: Boolean,
        navController: NavController
    ) {
        val graph = navController.navInflater.inflate(getMainNavigationGraphId())
        graph.setStartDestination(
            if (isShown) {
                if (isTokenNotOutdated) {
                    getTabsDestination()
                } else {
                    getSignInDestination()
                }
            } else {
                getOnBoardingDestination()
            }
        )
        navController.graph = graph
    }

    private fun onNavControllerActivated(navController: NavController) {
        if (this.navController == navController) return
        this.navController?.removeOnDestinationChangedListener(destinationListener)
        navController.addOnDestinationChangedListener(destinationListener)
        this.navController = navController
    }

    private fun getRootNavController(): NavController {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        return navHost.navController
    }

    private val destinationListener =
        NavController.OnDestinationChangedListener { _, destination, arguments ->
            supportActionBar?.title = prepareTitle(destination.label, arguments)
            supportActionBar?.setDisplayHomeAsUpEnabled(!isStartDestination(destination))
        }

    private fun isStartDestination(destination: NavDestination?): Boolean {
        if (destination == null) return false
        val graph = destination.parent ?: return false
        val startDestinations = topLevelDestinations + graph.startDestinationId
        return startDestinations.contains(destination.id)
    }

    private fun prepareTitle(label: CharSequence?, arguments: Bundle?): String {

        // code for this method has been copied from Google sources :)

        if (label == null) return ""
        val title = StringBuffer()
        val fillInPattern = Pattern.compile("\\{(.+?)\\}")
        val matcher = fillInPattern.matcher(label)
        while (matcher.find()) {
            val argName = matcher.group(1)
            if (arguments != null && arguments.containsKey(argName)) {
                matcher.appendReplacement(title, "")
                title.append(arguments[argName].toString())
            } else {
                throw IllegalArgumentException(
                    "Could not find $argName in $arguments to fill label $label"
                )
            }
        }
        matcher.appendTail(title)
        return title.toString()
    }

    private fun isShown(): Boolean {
        val bundle = intent.extras ?: throw IllegalStateException("No required arguments")
        val args = MainActivityArgs.fromBundle(bundle)
        return args.isShown
    }

    private fun isTokenNotOutdated(): Boolean {
        val bundle = intent.extras ?: throw IllegalStateException("No required arguments")
        val args = MainActivityArgs.fromBundle(bundle)
        return args.isTokenNotOutdated
    }

    private fun getMainNavigationGraphId(): Int = R.navigation.main_graph

    private fun getTabsDestination(): Int = R.id.tabsFragment

    private fun getSignInDestination(): Int = R.id.signInFragment

    private fun getOnBoardingDestination(): Int = R.id.onboadingFragment2

    private fun showSnackBar(uri: Uri) {
        Log.d("ProcessLog", "showSnackBar in MainActivity")
        Snackbar.make(
            binding.fragmentContainer,
            "Фото сохранено на устройство",
            Snackbar.LENGTH_SHORT
        ).setAnchorView(R.id.bottomNavigationView)
            .setAction("Перейти") {
                if (hasPermission()) {
                    goToGallery(uri)
                } else {
                    Log.d("ProcessLog", "hasPermission is false")
                    requestPermission()
                }
            }
            .show()
    }

    /** Вот тут у меян проблема, я пытаюсь открыть фото через стороннее приложение, но
     * во время этого, мне выдется надпись, что у меян нет доступа туда.*/
    private fun goToGallery(uri: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
//        val photoURI = FileProvider.getUriForFile(
//            this@MainActivity, BuildConfig.APPLICATION_ID + ".provider",
//
//        )
        intent.setDataAndType(uri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    /* Запрашиваем разрешения на запись и чтение файлов */
    private fun requestPermission() {
        requestPermissionLauncher.launch(*PERMISSIONS.toTypedArray())
    }

    private fun initPermissionResultListener() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { permissionToGrantedMap: Map<String, Boolean> ->
                if (permissionToGrantedMap.values.all { it }) {
                    Log.d("ProcessLog", "initPermissionResultListener")
                    goToGallery(Uri.parse("file://" + "/sdcard/DCIM"))//"content://com.android.externalstorage.documents/tree/primary/Pictures/Unsplash"))
                }
            }
    }

    private fun hasPermission(): Boolean {
        Log.d("StorageScopeLogging", "hasPermission ")
        return PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        const val INTENT_FILTER_SNACKBAR = "com.example.unsplash.show_snackBar"
        const val INTENT_FILTER_SNACKBAR_URI = "com.example.unsplash.show_snackBar_uri"
        private val PERMISSIONS = listOfNotNull(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                .takeIf { Build.VERSION.SDK_INT < Build.VERSION_CODES.Q },
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}