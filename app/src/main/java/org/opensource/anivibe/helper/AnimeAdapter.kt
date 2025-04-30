package org.opensource.anivibe.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

    private var savedList: List<Result> = emptyList()

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

        val imageUrl = anime.imageUrl?.jpg?.imagesUrl

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.moon_icon)
                .fit()
                .centerCrop()
                .into(holder.image)

        } else {
            holder.image.setImageResource(R.drawable.moon_icon)
        }
        holder.itemView.setOnClickListener {
            AnimeDetailsBottomSheet.newInstance(anime)
                .show((parentActivity as AppCompatActivity).supportFragmentManager, "AnimeDetailsBottomSheet")
        }
    }

    override fun getItemCount(): Int = animeList.size

    fun updateList(newList: List<Result>) {
        animeList = newList
        notifyDataSetChanged()
    }
}