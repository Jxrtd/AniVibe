package org.opensource.anivibe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.opensource.anivibe.data.User

object UserRepository {
    private const val USER_PREFS = "UserPrefs"
    private const val PROFILE_PREFS = "ProfilePrefs"
    private val profileUpdateListeners = mutableListOf<() -> Unit>()

    // Get current user from preferences
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

        notifyProfileUpdated()
    }

    fun updateUsername(context: Context, oldUsername: String, newUsername: String): Boolean {
        if (newUsername.isBlank()) return false

        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)

        // Update in SharedPreferences
        profilePrefs.edit().putString("username", newUsername).apply()

        // Update in all posts and comments
        PostRepository.updateUserInfo(context, oldUsername, newUsername,
            profilePrefs.getString("profile_image", null))

        notifyProfileUpdated()
        return true
    }

    /**
     * Gets the current username safely with fallback
     */
    fun getCurrentUsername(context: Context): String {
        val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)
        return profilePrefs.getString("username", null) ?: run {
            getCurrentUser(context).username.takeIf { it.isNotBlank() }
                ?: generateDefaultUsername(context)
        }
    }


    fun saveUsername(context: Context, username: String) {
        context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString("username", username)
            .apply()
        notifyProfileUpdated()
    }


    fun saveProfileImage(context: Context, bitmap: Bitmap) {
        val filename = "profile_${System.currentTimeMillis()}.png"

        try {
            // Make sure we're only using the filename without any path
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            // Update profile image path in user preferences
            val profilePrefs = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)
            val username = getCurrentUsername(context)

            // Store just the filename to preferences
            profilePrefs.edit().putString("profile_image", filename).apply()

            // Update current user's profile image path
            val user = getCurrentUser(context)
            user.profileImagePath = filename
            saveUser(context, user)

            // Important: Update all posts with this user's new profile image path
            PostRepository.updateProfileImage(context, username, filename)

            notifyProfileUpdated()
        } catch (e: Exception) {
            e.printStackTrace()
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

    /**
     * Add a listener to be notified when profile information changes
     */
    fun addProfileUpdateListener(listener: () -> Unit) {
        profileUpdateListeners.add(listener)
    }

    fun getUser(context: Context, username: String): User? {
        val currentUser = getCurrentUser(context)
        return if (currentUser.username == username) {
            currentUser
        } else {
            null // Or implement proper user lookup if you have multiple users
        }
    }
    /**
     * Remove a previously added profile update listener
     */
    fun removeProfileUpdateListener(listener: () -> Unit) {
        profileUpdateListeners.remove(listener)
    }

    /**
     * Notify all listeners that profile information has been updated
     */
    private fun notifyProfileUpdated() {
        profileUpdateListeners.forEach { it.invoke() }
    }
}