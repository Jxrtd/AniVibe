package org.opensource.anivibe.helper

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.anime.AnimeDetailsBottomSheet
import org.opensource.anivibe.anime.Result
import org.opensource.anivibe.data.SharedAnimeViewModel

class AnimeAdapter(
    private val parentActivity: Context,
    private var animeList: List<Result>
) : RecyclerView.Adapter<AnimeAdapter.CustomViewHolder>() {

    // Live ViewModel to manage saved anime
    private val viewModel by lazy {
        ViewModelProvider(parentActivity as AppCompatActivity)[SharedAnimeViewModel::class.java]
    }

    // Current list of saved anime
    private var savedList: List<Result> = emptyList()

    // Update saved list and refresh UI
    fun setSavedList(list: List<Result>) {
        savedList = list
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val image: ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.anime_item_layout, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val anime = animeList[position]
        holder.name.text = anime.title ?: "Unknown"

        // Load image
        val imageUrl = anime.imageUrl?.jpg?.imagesUrl
        if (imageUrl.isNullOrEmpty()) {
            holder.image.setImageResource(R.drawable.moon_icon)
        } else {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.moon_icon)
                .into(holder.image)
        }

        // Show details bottom sheet using factory
        holder.itemView.setOnClickListener {
            AnimeDetailsBottomSheet.newInstance(anime)
                .show((parentActivity as AppCompatActivity).supportFragmentManager, "AnimeDetailsBottomSheet")
        }

        // Determine if this anime is saved
        val isSaved = savedList.any { it.malId == anime.malId }
        Log.d("AnimeAdapter", "Anime: ${anime.title}, malId: ${anime.malId}, isSaved: $isSaved")

        // Update save button appearance based on saved status
        if (isSaved) {
            // If saved, change to a delete icon with red tint
        } else {
            // If not saved, use save icon
        }

    }

    override fun getItemCount(): Int = animeList.size

    // Update full anime list
    fun updateList(newList: List<Result>) {
        animeList = newList
        notifyDataSetChanged()
    }
}