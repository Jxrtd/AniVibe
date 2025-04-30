package org.opensource.anivibe.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import java.io.File

object ProfileImageUtils {

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

}