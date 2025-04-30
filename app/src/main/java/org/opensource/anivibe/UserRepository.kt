package org.opensource.anivibe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.opensource.anivibe.data.User

object UserRepository {
    private const val USER_PREFS = "UserPrefs"
    private const val PROFILE_PREFS = "ProfilePrefs"
    private val profileUpdateListeners = mutableListOf<() -> Unit>()

    fun getCurrentUser(context: Context): User {
        val userPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)

        return User(
            username = profilePrefs.getString("username", "")?.takeIf { it.isNotBlank() }
                ?: generateDefaultUsername(context),
            email = userPrefs.getString("email", "") ?: "",
            education = profilePrefs.getString("education", "") ?: "",
            hometown = profilePrefs.getString("hometown", "") ?: "",
            location = profilePrefs.getString("location", "") ?: "",
            birthdate = profilePrefs.getString("birthdate", "") ?: "",
            profileImagePath = profilePrefs.getString("profile_image", null)
        )
    }

    private fun generateDefaultUsername(context: Context): String {
        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)
        val defaultName = "User_${System.currentTimeMillis() % 10000}"
        profilePrefs.edit().putString("username", defaultName).apply()
        return defaultName
    }

    fun saveUser(context: Context, user: User) {
        val userPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)

        with(userPrefs.edit()) {
            putString("email", user.email)
            apply()
        }

        with(profilePrefs.edit()) {
            putString("username", user.username)
            putString("education", user.education)
            putString("hometown", user.hometown)
            putString("location", user.location)
            putString("birthdate", user.birthdate)
            if (user.profileImagePath != null) {
                putString("profile_image", user.profileImagePath)
            }
            apply()
        }

        notifyProfileUpdated()
    }

    fun getCurrentUsername(context: Context): String {
        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)
        return profilePrefs.getString("username", null) ?: run {
            getCurrentUser(context).username.takeIf { it.isNotBlank() }
                ?: generateDefaultUsername(context)
        }
    }

    fun getProfileImageSafely(context: Context): Bitmap? {
        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)
        val imagePath = profilePrefs.getString("profile_image", null) ?: return null

        return try {
            val filename = if (imagePath.contains("/")) {
                imagePath.substring(imagePath.lastIndexOf("/") + 1)
            } else {
                imagePath
            }

            context.openFileInput(filename).use { fis ->
                BitmapFactory.decodeStream(fis)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun addProfileUpdateListener(listener: () -> Unit) {
        profileUpdateListeners.add(listener)
    }

    fun removeProfileUpdateListener(listener: () -> Unit) {
        profileUpdateListeners.remove(listener)
    }

    private fun notifyProfileUpdated() {
        profileUpdateListeners.forEach { it.invoke() }
    }
}