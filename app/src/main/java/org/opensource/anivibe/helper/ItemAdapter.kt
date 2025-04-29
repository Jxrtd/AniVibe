package org.opensource.anivibe.helper

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
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
import org.opensource.anivibe.data.Item
import java.io.File

class ItemAdapter(
    private val context: Context,
    private var itemList: MutableList<Item>,
    private val onDeleteClickListener: (Int) -> Unit,
    private val onLikeClickListener: (Int) -> Unit,
    private val onCommentClickListener: (Int) -> Unit,
    private val timestampFormatter: (Long) -> String // 🔥 Added timestampFormatter here
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

        // Load profile image
        ProfileImageUtils.loadProfileImage(context, holder.profile, item.profileImagePath)

        holder.description.text = item.description
        holder.username.text = item.username
        holder.timestamp.text = timestampFormatter(item.timestamp) // 🔥 Use timestampFormatter

        // Set click listeners
        holder.deleteButton.setOnClickListener { onDeleteClickListener(position) }
        holder.likeButton.setOnClickListener {
            item.isLiked = !item.isLiked
            updateLikeButton(holder.likeButton, item.isLiked)
            onLikeClickListener(position)
        }
        holder.commentButton.setOnClickListener { onCommentClickListener(position) }

        // Update like button visual
        updateLikeButton(holder.likeButton, item.isLiked)
    }

    private fun loadProfileImage(imageView: ImageView, profileImagePath: String?) {
        try {
            when {
                profileImagePath.isNullOrBlank() -> {
                    imageView.setImageResource(R.drawable.profile_circle)
                }
                profileImagePath.startsWith("http") -> {
                    Picasso.get()
                        .load(profileImagePath)
                        .placeholder(R.drawable.profile_circle)
                        .error(R.drawable.profile_circle)
                        .transform(CircleTransform())
                        .into(imageView)
                }
                else -> {
                    val imageFile = File(profileImagePath)
                    if (imageFile.exists()) {
                        Picasso.get()
                            .load(imageFile)
                            .placeholder(R.drawable.profile_circle)
                            .error(R.drawable.profile_circle)
                            .transform(CircleTransform())
                            .into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.profile_circle)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading profile image", e)
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

    fun removeItem(position: Int) {
        if (position in itemList.indices) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateItems(newItems: List<Item>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }
}
