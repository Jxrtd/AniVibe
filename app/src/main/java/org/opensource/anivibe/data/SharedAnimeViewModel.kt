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
        // Load saved anime from shared preferences
        loadSavedAnime(application)
    }

    /// Add anime only if it's not already in the list
    fun addAnime(anime: Result, context: Context) {
        val currentList = savedAnimeList.value ?: emptyList()
        if (currentList.none { it.malId == anime.malId }) {
            val newList = currentList + anime
            _savedAnimeList.value = newList
            // Save to persistent storage
            saveSavedAnimeList(context)  // THIS LINE WAS COMMENTED OUT
        }
    }

    // Remove anime by malId
    fun removeAnime(anime: Result, context: Context) {
        val currentList = savedAnimeList.value ?: emptyList()
        val newList = currentList.filter { it.malId != anime.malId }
        _savedAnimeList.value = newList
        // Save to persistent storage
        saveSavedAnimeList(context)  // THIS LINE WAS COMMENTED OUT
    }

    fun isAnimeSaved(malId: Int?): Boolean {
        if (malId == null) return false
        return savedAnimeList.value?.any { it.malId == malId } ?: false
    }

    fun getSavedList(): List<Result> {
        return _savedAnimeList.value ?: emptyList()
    }

    private fun saveSavedAnimeList(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(_savedAnimeList.value)
        editor.putString(SAVED_ANIME_KEY, json)
        editor.apply()

        // Debug log
        Log.d("SharedAnimeViewModel", "Saved anime list to SharedPreferences, JSON size: ${json?.length ?: 0}")
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

                // Debug log
                Log.d("SharedAnimeViewModel", "Loaded saved anime list. Size: ${savedAnimeList.size}")
                Log.d("SharedAnimeViewModel", "Loaded anime IDs: ${savedAnimeList.map { it.malId }}")
            } catch (e: Exception) {
                Log.e("SharedAnimeViewModel", "Error parsing saved anime JSON: ${e.message}")
                // Reset the saved data if there's an error
                _savedAnimeList.value = emptyList()
                sharedPreferences.edit().remove(SAVED_ANIME_KEY).apply()
            }
        } else {
            Log.d("SharedAnimeViewModel", "No saved anime list found in SharedPreferences")
        }
    }
}