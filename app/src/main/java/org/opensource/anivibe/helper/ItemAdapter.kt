package org.opensource.anivibe.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.R
import org.opensource.anivibe.data.Item

class ItemAdapter(
    private val context: Context,
    private var itemList: MutableList<Item>, // Use MutableList to modify items
    private val onDeleteClickListener: (Int) -> Unit // Callback for delete action
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profile: ImageView = view.findViewById(R.id.profile_pic_post)
        val description: TextView = view.findViewById(R.id.content)
        val username: TextView = view.findViewById(R.id.itemusername)
        val deleteButton: ImageView = view.findViewById(R.id.btndelete) // Find delete button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.profile.setImageResource(item.profileImageResId)
        holder.description.text = item.description
        holder.username.text = item.username

        // Set delete button click listener
        holder.deleteButton.setOnClickListener {
            onDeleteClickListener(position) // Pass the position to the callback
        }
    }

    override fun getItemCount(): Int = itemList.size

    // Function to remove an item and notify RecyclerView
    fun removeItem(position: Int) {
        if (position in itemList.indices) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
