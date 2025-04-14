package org.opensource.anivibe.anime.services

import org.opensource.anivibe.anime.Result
import org.opensource.anivibe.anime.SavedAnime
import org.opensource.anivibe.anime.SearchedAnime.SearchedAnime
import org.opensource.anivibe.anime.TopAnime.TopAnime
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeService {

    @GET("top/anime")
    fun getTopAnime(
        @Query("limit") limit: Int = 25,
        @Query("page") page: Int = 1
    ): Call<TopAnime>

    @GET("anime")
    fun getSearchAnime(
        @Query("q") query: String,
        @Query("limit") limit: Int = 25,
        @Query("page") page: Int = 1
    ): Call<SearchedAnime>

    // New Endpoint for Saved Anime
    @GET("saved/anime")
    fun getSavedAnime(): Call<SavedAnime>

    companion object {
        private const val BASE_URL = "https://api.jikan.moe/v4/"

        fun create(): AnimeService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AnimeService::class.java)
        }
    }
}
