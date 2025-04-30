package org.opensource.anivibe.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.UserRepository
import org.opensource.anivibe.data.Item
import java.io.File

class ItemAdapter(
    private val context: Context,
    private var itemList: MutableList<Item>,
    private val onDeleteClickListener: (Int) -> Unit,
    private val onLikeClickListener: (Int) -> Unit,
    private val onCommentClickListener: (Int) -> Unit,
    private val timestampFormatter: (Long) -> String
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profile: ImageView = view.findViewById(R.id.profile_pic_post)
        val description: TextView = view.findViewById(R.id.content)
        val username: TextView = view.findViewById(R.id.itemusername)
        val timestamp: TextView = view.findViewById(R.id.post_timestamp)
        val deleteButton: ImageView = view.findViewById(R.id.btndelete)
        val likeButton: ImageButton = view.findViewById(R.id.likebtn)
        val commentButton: ImageButton = view.findViewById(R.id.commentbtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]

        ProfileImageUtils.loadProfileImage(context, holder.profile, item.profileImagePath)

        holder.description.text = item.description
        holder.username.text = item.username
        holder.timestamp.text = timestampFormatter(item.timestamp)

        updateLikeButton(holder.likeButton, item.isLiked)

        holder.deleteButton.setOnClickListener {
            onDeleteClickListener(position)
        }

        holder.likeButton.setOnClickListener {
            onLikeClickListener(position)
            item.isLiked = !item.isLiked
            updateLikeButton(holder.likeButton, item.isLiked)
        }

        holder.commentButton.setOnClickListener {
            onCommentClickListener(position)
        }

        loadProfileImage(context, holder.profile, item.profileImagePath)
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

    private fun updateLikeButton(button: ImageButton, isLiked: Boolean) {
        if (isLiked) {
            button.setImageResource(R.drawable.ic_heart_filled)
            button.setColorFilter(ContextCompat.getColor(context, R.color.red))
        } else {
            button.setImageResource(R.drawable.ic_heart_outline)
            button.setColorFilter(ContextCompat.getColor(context, R.color.white))
        }
    }

    override fun getItemCount(): Int = itemList.size

}