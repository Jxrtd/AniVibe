package org.opensource.anivibe.data

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.opensource.anivibe.anime.Result

class SharedAnimeViewModel(application: Application) : AndroidViewModel(application) {

    private val _savedAnimeList = MutableLiveData<List<Result>>(emptyList())
    val savedAnimeList: LiveData<List<Result>> get() = _savedAnimeList
    private val PREFS_NAME = "anime_preferences"
    private val SAVED_ANIME_KEY = "saved_anime_list"

    init {
        loadSavedAnime(application)
    }

    fun addAnime(anime: Result, context: Context) {
        val currentList = savedAnimeList.value ?: emptyList()
        if (currentList.none { it.malId == anime.malId }) {
            val newList = currentList + anime
            _savedAnimeList.value = newList
            saveSavedAnimeList(context)
        }
    }

    fun removeAnime(anime: Result, context: Context) {
        val currentList = savedAnimeList.value ?: emptyList()
        val newList = currentList.filter { it.malId != anime.malId }
        _savedAnimeList.value = newList
        saveSavedAnimeList(context)
    }

    fun isAnimeSaved(malId: Int?): Boolean {
        if (malId == null) return false
        return savedAnimeList.value?.any { it.malId == malId } ?: false
    }

    private fun saveSavedAnimeList(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(_savedAnimeList.value)
        editor.putString(SAVED_ANIME_KEY, json)
        editor.apply()
    }

    private fun loadSavedAnime(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(SAVED_ANIME_KEY, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Result>>() {}.type
                val savedAnimeList = gson.fromJson<List<Result>>(json, type)
                _savedAnimeList.value = savedAnimeList
            } catch (e: Exception) {
                _savedAnimeList.value = emptyList()
                sharedPreferences.edit().remove(SAVED_ANIME_KEY).apply()
            }
        }
    }
}