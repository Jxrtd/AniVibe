package org.opensource.anivibe.helper

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
import org.opensource.anivibe.R
import org.opensource.anivibe.data.Comment
import java.io.File

// Circle transformation for Picasso
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
        holder.username.text = comment.username
        holder.content.text = comment.content

        // Handle profile image properly with circular transformation
        if (!comment.profileImagePath.isNullOrBlank()) {
            val imageFile = File(holder.itemView.context.filesDir, comment.profileImagePath!!)

            if (imageFile.exists()) {
                // Load from file if it exists
                Picasso.get()
                    .load(imageFile)
                    .placeholder(R.drawable.profile_circle)
                    .error(R.drawable.profile_circle)
                    .transform(CircleTransform())
                    .into(holder.profilePic)
            } else {
                // Try to load from filepath string directly
                Picasso.get()
                    .load(comment.profileImagePath)
                    .placeholder(R.drawable.profile_circle)
                    .error(R.drawable.profile_circle)
                    .transform(CircleTransform())
                    .into(holder.profilePic)
            }
        } else {
            // Set default profile picture
            holder.profilePic.setImageResource(R.drawable.profile_circle)
        }
    }

    override fun getItemCount(): Int = comments.size
}