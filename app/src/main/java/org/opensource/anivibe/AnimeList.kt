package org.opensource.anivibe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.anime.AnimeDetailsBottomSheet
import org.opensource.anivibe.anime.Result
import org.opensource.anivibe.anime.SearchedAnime.SearchedAnime
import org.opensource.anivibe.anime.TopAnime.TopAnime
import org.opensource.anivibe.anime.services.AnimeSevice
import org.opensource.anivibe.databinding.FragmentAnimeListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnimeList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentAnimeListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            val animeService = AnimeSevice.create()
            val call = animeService.getTopAnime()

            call.enqueue(object : Callback<TopAnime> {

                override fun onResponse(call: Call<TopAnime>, response: Response<TopAnime>) {
                    if (response.body() != null) {
                        val top = response.body()!!.data
                        animeRecyclerView.adapter = AnimeAdapter(this@AnimeList,top)
                        animeRecyclerView.layoutManager = GridLayoutManager(this@AnimeList,3)
                    }
                }

                override fun onFailure(call: Call<TopAnime>, t: Throwable) {

                }

            })

            btnSearch.setOnClickListener {
                val searchedAnime = searchInputEditText.text.toString().trim()

                if (searchedAnime.isNotEmpty()) {
                    val callSearchedAnime = animeService.getSearchAnime(searchedAnime)

                    callSearchedAnime.enqueue(object : Callback<SearchedAnime> {
                        override fun onResponse(
                            call: Call<SearchedAnime>,
                            response: Response<SearchedAnime>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                val searchedAnimes = response.body()!!.data
                                animeRecyclerView.adapter = AnimeAdapter(this@AnimeList, searchedAnimes)
                                animeRecyclerView.layoutManager = GridLayoutManager(this@AnimeList, 3)
                            } else {
                                Toast.makeText(this@AnimeList, "No anime found!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<SearchedAnime>, t: Throwable) {
                            Toast.makeText(this@AnimeList, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Toast.makeText(this@AnimeList, "Please enter an anime name", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    class AnimeAdapter(
        private val parentActivity: AppCompatActivity,
        private val anime: List<Result>
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

    }

}
