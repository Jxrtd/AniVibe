package org.opensource.anivibe.helper

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.anime.AnimeDetailsBottomSheet
import org.opensource.anivibe.anime.Result
import org.opensource.anivibe.data.SharedAnimeViewModel

class SaveAdapter(
    private val context: Context,
    private var animeList: List<Result>
) : RecyclerView.Adapter<SaveAdapter.AnimeViewHolder>() {

    private val viewModel by lazy {
        (context as AppCompatActivity).let {
            ViewModelProvider(it)[SharedAnimeViewModel::class.java]
        }
    }

    inner class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val image: ImageView = view.findViewById(R.id.image)
        // We're removing this button reference as we're no longer using it
        // val removeButton: ImageButton = view.findViewById(R.id.btn_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_anime_item_layout, parent, false)
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

        // Show anime details on click
        holder.itemView.setOnClickListener {
            val activity = context as? AppCompatActivity
            activity?.supportFragmentManager?.let { fm ->
                AnimeDetailsBottomSheet.newInstance(animeItem).show(fm, "AnimeDetailsBottomSheet")
            }
        }

        // We're removing the click listener for the remove button since it's no longer in the layout
    }

    override fun getItemCount(): Int = animeList.size

    fun updateList(newList: List<Result>) {
        animeList = newList.toList() // Create new list instance
        notifyDataSetChanged()
    }
}