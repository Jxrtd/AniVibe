package org.opensource.anivibe.helper

import android.content.Context
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

class SaveAdapter(
    private val context: Context,
    private var animeList: List<Result>
) : RecyclerView.Adapter<SaveAdapter.AnimeViewHolder>() {

    inner class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val image: ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.anime_item_layout, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val animeItem = animeList[position]
        holder.name.text = animeItem.title ?: "Unknown Title"

        val imageUrl = animeItem.imageUrl?.jpg?.imagesUrl
        if (imageUrl.isNullOrEmpty()) {
            holder.image.setImageResource(R.drawable.moon_icon)
        } else {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.moon_icon)
                .into(holder.image)
        }

        holder.itemView.setOnClickListener {
            val activity = context as? AppCompatActivity
            activity?.supportFragmentManager?.let { fm ->
                AnimeDetailsBottomSheet.newInstance(animeItem).show(fm, "AnimeDetailsBottomSheet")
            }
        }
    }

    override fun getItemCount(): Int = animeList.size

    fun updateList(newList: List<Result>) {
        animeList = newList.toList() // Create new list instance
        notifyDataSetChanged()
    }
}