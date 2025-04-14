package org.opensource.anivibe.helper

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.anime.AnimeDetailsBottomSheet
import org.opensource.anivibe.anime.Result

class AnimeAdapter(
    private val parentActivity: Context,
    private var anime: List<Result>
) : RecyclerView.Adapter<AnimeAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.anime_item_layout, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val anime = anime[position]
        val view = holder.itemView

        // Safely find and bind views
        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)

        // Set anime title
        name.text = anime.title ?: "Unknown"

        // Load anime image with fallback
        val imageUrl = anime.imageUrl.jpg.imagesUrl
        Log.d("AnimeDetails", "Image URL: $anime")
        if (imageUrl.isNullOrEmpty()) {
            image.setImageResource(R.drawable.moon_icon) // Default placeholder
        } else {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.moon_icon)
                .into(image)
        }

        // Handle click event to show AnimeDetailsBottomSheet
        view.setOnClickListener {
            AnimeDetailsBottomSheet(anime).apply {
                show((view.context as AppCompatActivity).supportFragmentManager, "AnimeDetailsBottomSheet")
            }
        }
    }


    override fun getItemCount(): Int {
        return anime.size
    }

    fun updateList(newList: List<Result>) {
        anime = newList // Update list
        notifyDataSetChanged() // Notify RecyclerView to refresh
    }

}