package org.opensource.anivibe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import org.opensource.anivibe.anime.SearchedAnime.SearchedAnime
import org.opensource.anivibe.anime.TopAnime.TopAnime
import org.opensource.anivibe.anime.services.AnimeService
import org.opensource.anivibe.databinding.FragmentAnimeListBinding
import org.opensource.anivibe.helper.AnimeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnimeList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentAnimeListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            val animeService = AnimeService.create()
            val call = animeService.getTopAnime()

            call.enqueue(object : Callback<TopAnime> {

                override fun onResponse(call: Call<TopAnime>, response: Response<TopAnime>) {
                    if (response.body() != null) {
                        val top = response.body()!!.data
                        animeRecyclerView.adapter = AnimeAdapter(this@AnimeList,top)
                        animeRecyclerView.layoutManager = GridLayoutManager(this@AnimeList,2)
                    }
                }

                override fun onFailure(call: Call<TopAnime>, t: Throwable) {

                }

            })
            btnback.setOnClickListener {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
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
                                animeRecyclerView.layoutManager = GridLayoutManager(this@AnimeList, 2)
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


}