package org.opensource.anivibe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.opensource.anivibe.data.User

object UserRepository {
    private const val USER_PREFS = "UserPrefs"
    private const val PROFILE_PREFS = "ProfilePrefs"

    // Get current user from preferences
    fun getCurrentUser(context: Context): User {
        val userPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)

        return User(
            username = profilePrefs.getString("username", "") ?: "",
            email = userPrefs.getString("email", "") ?: "",
            education = profilePrefs.getString("education", "") ?: "",
            hometown = profilePrefs.getString("hometown", "") ?: "",
            location = profilePrefs.getString("location", "") ?: "",
            birthdate = profilePrefs.getString("birthdate", "") ?: "",
            profileImagePath = profilePrefs.getString("profile_image", null)
        )
    }

    // Save user to preferences
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
    }

    // Verify password
    fun verifyPassword(context: Context, password: String): Boolean {
        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)
        val storedPassword = profilePrefs.getString("password", "") ?: ""
        return storedPassword == password
    }

    // Update password
    fun updatePassword(context: Context, newPassword: String) {
        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)
        profilePrefs.edit().putString("password", newPassword).apply()
    }

    // Get profile image
    fun getProfileImageSafely(context: Context): Bitmap? {
        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)
        val imagePath = profilePrefs.getString("profile_image", null) ?: return null

        return try {
            // Extract just the filename if it contains path separators
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

    // Replace saveProfileImage with this improved version
    fun saveProfileImage(context: Context, bitmap: Bitmap) {
        val filename = "profile_${System.currentTimeMillis()}.png"

        try {
            // Make sure we're only using the filename without any path
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            // Update profile image path in user preferences
            val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)
            profilePrefs.edit().putString("profile_image", filename).apply()

            // Update current user's profile image path
            val user = getCurrentUser(context)
            user.profileImagePath = filename
            saveUser(context, user)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}