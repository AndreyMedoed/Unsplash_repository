package com.example.unsplash.screens.main.tabs.profile_fragment


import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import com.example.unsplash.Network.NetworkConfig
import com.example.unsplash.dataBase.Database
import com.example.unsplash.dataBase.adapters.DatabaseProfileAdapter
import com.example.unsplash.dataBase.adapters.DatabaseUserAdapter
import com.example.unsplash.utils.isInternetAvailable

class ProfileRepository(private val context: Context) {

    private val collectionDao = Database.instance.collectionDao()
    private val collectionLinksDao = Database.instance.collectionLinksDao()
    private val photoDao = Database.instance.photoDao()
    private val photoUrlDao = Database.instance.photoUrlDao()
    private val profileDao = Database.instance.profileDao()
    private val profileImageDao = Database.instance.profileImageDao()
    private val remoteKeyDao = Database.instance.remoteKeyDao()
    private val tokenDao = Database.instance.tokenDao()
    private val userDao = Database.instance.userDao()
    private val userLinksDao = Database.instance.userLinksDao()

    private val sharedPreferences by lazy {
        context.getSharedPreferences(
            ProfileFragment.SHARED_PREFERENCE_NAME_PROFILE,
            Context.MODE_PRIVATE
        )
    }

    private val profileAdapter = DatabaseProfileAdapter()
    private val userAdapter = DatabaseUserAdapter()


    suspend fun getMyProfile(): Profile? {
        return if (isInternetAvailable(context)) {
            val profile = NetworkConfig.unsplashApi.getMyProfile()
            val profileDBid = profile?.let { profileAdapter.fromProfileToDBProfile(profile) }
            putIdInSharedPref(profileDBid, ProfileFragment.PROFILE_ID_KEY)
            profile
        } else {
            val profileId = sharedPreferences.getString(ProfileFragment.PROFILE_ID_KEY, "")
            profileAdapter.fromDBProfileToProfile(profileId)
        }
    }

    suspend fun getUser(username: String): User? {
        return if (isInternetAvailable(context)) {
            val user = NetworkConfig.unsplashApi.getUser(username)
            val userDBid = user?.let {
                userAdapter.fromUserToDBUser(
                    user,
                    ProfileFragment.PROFILE_MY_USER_MARK
                )
            }
            putIdInSharedPref(userDBid, ProfileFragment.PROFILE_USER_ID_KEY)
            user
        } else {
            val userId = sharedPreferences.getString(ProfileFragment.PROFILE_USER_ID_KEY, "")
            userAdapter.fromDBUserToUser(userId)
        }
    }

    suspend fun getCollectionPhotos(collectionId: String): List<Photo>? =
        NetworkConfig.unsplashApi.getCollectionPhotos(collectionId)

    suspend fun clearAllDatabase() {
        Database.instance.withTransaction {
            collectionDao.clearAll()
            collectionLinksDao.clearAll()
            photoDao.clearAll()
            profileDao.clearAll()
            remoteKeyDao.clearAll()
            tokenDao.clearAll()
            userDao.clearAll()
            userLinksDao.clearAll()
            photoUrlDao.clearAll()
            profileImageDao.clearAll()
        }
    }

    private fun putIdInSharedPref(id: String?, key: String) {
        sharedPreferences.edit()
            .putString(
                key,
                id
            ).apply()
        Log.d("bundleLogging", "putStateInSharedPref")
    }
}