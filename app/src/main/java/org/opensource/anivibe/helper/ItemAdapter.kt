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

class ItemAdapter(private val context: Context, private val itemList: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profile: ImageView = view.findViewById(R.id.profile_pic_post)
        val description: TextView = view.findViewById(R.id.content)
        val username: TextView = view.findViewById(R.id.itemusername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.profile.setImageResource(item.profileImageResId) // Fixed: Set Image Resource
        holder.description.text = item.description
        holder.username.text = item.username // Fixed: Set Username
    }

    override fun getItemCount(): Int = itemList.size
}
