package org.opensource.anivibe.anime.services

import org.opensource.anivibe.anime.SearchedAnime.SearchedAnime
import org.opensource.anivibe.anime.TopAnime.TopAnime
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeSevice {

    @GET("top/anime")
    fun getTopAnime(): Call<TopAnime>

    @GET("anime")
    fun getSearchAnime(@Query("q") query: String): Call<SearchedAnime>

    companion object {
        fun create(): AnimeSevice {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.jikan.moe/v4/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(AnimeSevice::class.java)
        }
    }
}