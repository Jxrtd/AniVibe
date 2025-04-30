package org.opensource.anivibe.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import java.io.File

object ProfileImageUtils {
    private const val TAG = "ProfileImageUtils"

    fun getFilenameFromPath(path: String?): String? {
        if (path == null) return null
        return if (path.contains("/")) {
            path.substring(path.lastIndexOf("/") + 1)
        } else {
            path
        }
    }

    fun loadProfileImage(context: Context, imageView: ImageView, profileImagePath: String?) {
        if (profileImagePath.isNullOrBlank()) {
            imageView.setImageResource(R.drawable.profile_circle)
            return
        }

        try {
            val filename = getFilenameFromPath(profileImagePath)

            val internalFile = File(context.filesDir, filename)
            if (internalFile.exists()) {
                Picasso.get()
                    .load(internalFile)
                    .placeholder(R.drawable.profile_circle)
                    .error(R.drawable.profile_circle)
                    .transform(CircleTransform())
                    .into(imageView)
                return
            }

            val fileFromPath = File(profileImagePath)
            if (fileFromPath.exists()) {
                Picasso.get()
                    .load(fileFromPath)
                    .placeholder(R.drawable.profile_circle)
                    .error(R.drawable.profile_circle)
                    .transform(CircleTransform())
                    .into(imageView)
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
            imageView.setImageResource(R.drawable.profile_circle)
        }
    }

    fun loadProfileImageBitmap(context: Context, profileImagePath: String?): Bitmap? {
        if (profileImagePath.isNullOrBlank()) return null

        try {
            // Get just the filename
            val filename = getFilenameFromPath(profileImagePath)

            return context.openFileInput(filename).use { fis ->
                BitmapFactory.decodeStream(fis)
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun saveProfileImage(context: Context, bitmap: Bitmap): String {
        val filename = "profile_${System.currentTimeMillis()}.png"

        try {
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            return filename
        } catch (e: Exception) {
            throw e
        }
    }
}