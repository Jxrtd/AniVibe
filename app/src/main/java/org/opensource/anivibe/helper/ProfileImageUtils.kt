package org.opensource.anivibe.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import java.io.File

/**
 * Utility class to handle profile image operations consistently across the app
 */
object ProfileImageUtils {
    private const val TAG = "ProfileImageUtils"

    /**
     * Get the filename from a path string
     */
    fun getFilenameFromPath(path: String?): String? {
        if (path == null) return null
        return if (path.contains("/")) {
            path.substring(path.lastIndexOf("/") + 1)
        } else {
            path
        }
    }

    /**
     * Load a profile image into an ImageView, handling paths safely
     */
    fun loadProfileImage(context: Context, imageView: ImageView, profileImagePath: String?) {
        if (profileImagePath.isNullOrBlank()) {
            imageView.setImageResource(R.drawable.profile_circle)
            return
        }

        try {
            // Get just the filename
            val filename = getFilenameFromPath(profileImagePath)

            // Try loading from internal storage first
            val internalFile = File(context.filesDir, filename)
            if (internalFile.exists()) {
                Picasso.get()
                    .load(internalFile)
                    .placeholder(R.drawable.profile_circle)
                    .error(R.drawable.profile_circle)
                    .transform(CircleTransform())
                    .into(imageView)
                Log.d(TAG, "Loaded profile image from internal storage: $filename")
                return
            }

            // If that fails, try loading from the full path
            val fileFromPath = File(profileImagePath)
            if (fileFromPath.exists()) {
                Picasso.get()
                    .load(fileFromPath)
                    .placeholder(R.drawable.profile_circle)
                    .error(R.drawable.profile_circle)
                    .transform(CircleTransform())
                    .into(imageView)
                Log.d(TAG, "Loaded profile image from full path: $profileImagePath")
                return
            }

            // Finally try direct loading (might be a URL or resource)
            Picasso.get()
                .load(profileImagePath)
                .placeholder(R.drawable.profile_circle)
                .error(R.drawable.profile_circle)
                .transform(CircleTransform())
                .into(imageView)

        } catch (e: Exception) {
            Log.e(TAG, "Error loading profile image: $profileImagePath", e)
            imageView.setImageResource(R.drawable.profile_circle)
        }
    }

    /**
     * Load a profile image bitmap safely from internal storage
     */
    fun loadProfileImageBitmap(context: Context, profileImagePath: String?): Bitmap? {
        if (profileImagePath.isNullOrBlank()) return null

        try {
            // Get just the filename
            val filename = getFilenameFromPath(profileImagePath)

            return context.openFileInput(filename).use { fis ->
                BitmapFactory.decodeStream(fis)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading profile image bitmap: $profileImagePath", e)
            return null
        }
    }

    /**
     * Save a profile image bitmap to internal storage
     * Returns the filename (not full path) that was saved
     */
    fun saveProfileImage(context: Context, bitmap: Bitmap): String {
        val filename = "profile_${System.currentTimeMillis()}.png"

        try {
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Log.d(TAG, "Saved profile image: $filename")
            return filename
        } catch (e: Exception) {
            Log.e(TAG, "Error saving profile image", e)
            throw e
        }
    }
}