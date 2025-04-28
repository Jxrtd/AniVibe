package org.opensource.anivibe.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.util.Log
import org.opensource.anivibe.R
import org.opensource.anivibe.UserRepository
import org.opensource.anivibe.data.Comment
import java.io.File

// Circle transformation for Picasso (unchanged)
class CircleTransform : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(size, size, source.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        paint.shader = shader
        paint.isAntiAlias = true

        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)

        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val TAG = "CommentAdapter"

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.comment_username)
        val content: TextView = view.findViewById(R.id.comment_content)
        val profilePic: ImageView = view.findViewById(R.id.comment_profile_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        val context = holder.itemView.context

        Log.d(TAG, "Binding comment at position $position, userId: ${comment.userId}, username: ${comment.username}")

        // Always try to get the current username from UserRepository if userId is available
        if (!comment.userId.isNullOrEmpty()) {
            // Use UserRepository to get the current user's name
            val userPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val currentUsername = userPrefs.getString("username", null)

            // If the comment's userId matches the current user's saved username,
            // update the display name to show the current username
            if (comment.userId == currentUsername || comment.userId == comment.username) {
                Log.d(TAG, "Updating username display from ${comment.username} to $currentUsername")
                holder.username.text = currentUsername
            } else {
                // Otherwise use the stored username
                holder.username.text = comment.username
            }
        } else {
            // Fall back to the stored username if no userId is available
            holder.username.text = comment.username
            Log.d(TAG, "Using stored username: ${comment.username}")
        }

        holder.username.setTypeface(holder.username.typeface, Typeface.BOLD)
        holder.content.text = comment.content

        // Use ProfileImageUtils to load the profile image
        if (!comment.profileImagePath.isNullOrBlank()) {
            ProfileImageUtils.loadProfileImage(context, holder.profilePic, comment.profileImagePath)
        } else {
            // Set default profile picture
            Log.d(TAG, "No profile image path, using default")
            holder.profilePic.setImageResource(R.drawable.profile_circle)
        }
    }

    override fun getItemCount(): Int = comments.size
}