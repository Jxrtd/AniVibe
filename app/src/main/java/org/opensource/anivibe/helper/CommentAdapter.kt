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
import androidx.core.content.ContextCompat
import org.opensource.anivibe.R
import org.opensource.anivibe.UserRepository
import org.opensource.anivibe.data.Comment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

class CommentAdapter(
    private val comments: MutableList<Comment>,
    private val currentUsername: String? = null
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.comment_username)
        val content: TextView = view.findViewById(R.id.comment_content)
        val profilePic: ImageView = view.findViewById(R.id.comment_profile_pic)
        val timestamp: TextView? = view.findViewById(R.id.comment_timestamp)
        val deleteButton: ImageView = view.findViewById(R.id.delete_button)
    }

    interface OnCommentDeleteListener {
        fun onCommentDeleted(position: Int)
    }

    private var deleteListener: OnCommentDeleteListener? = null

    fun setOnCommentDeleteListener(listener: OnCommentDeleteListener) {
        this.deleteListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        val context = holder.itemView.context

        val isCurrentUserComment = currentUsername != null && comment.username == currentUsername
        holder.deleteButton.visibility = if (isCurrentUserComment) View.VISIBLE else View.VISIBLE

        holder.deleteButton.setOnClickListener {
            deleteListener?.onCommentDeleted(position)
        }

        holder.username.text = comment.username
        if (currentUsername != null && comment.username == currentUsername) {
            holder.username.setTextColor(ContextCompat.getColor(context, R.color.accentRed))
            holder.username.setTypeface(null, Typeface.BOLD)
        } else {
            holder.username.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.username.setTypeface(null, Typeface.NORMAL)
        }

        holder.content.text = comment.content

        holder.timestamp?.text = comment.timestamp.let {
            SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(it))
        } ?: ""

        loadProfileImage(context, holder.profilePic, comment.profileImagePath)
    }

    private fun loadProfileImage(context: Context, imageView: ImageView, path: String?) {
        try {
            when {
                path.isNullOrBlank() -> {
                    imageView.setImageResource(R.drawable.profile_circle)
                }
                path.startsWith("http") -> {
                    Picasso.get()
                        .load(path)
                        .placeholder(R.drawable.profile_circle)
                        .error(R.drawable.profile_circle)
                        .transform(CircleTransform())
                        .into(imageView)
                }
                else -> {
                    val imageFile = File(path)
                    if (imageFile.exists()) {
                        Picasso.get()
                            .load(imageFile)
                            .placeholder(R.drawable.profile_circle)
                            .error(R.drawable.profile_circle)
                            .transform(CircleTransform())
                            .into(imageView)
                    } else {
                        UserRepository.getProfileImageSafely(context)?.let {
                            imageView.setImageBitmap(it)
                        } ?: imageView.setImageResource(R.drawable.profile_circle)
                    }
                }
            }
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.profile_circle)
        }
    }

    override fun getItemCount(): Int = comments.size
}