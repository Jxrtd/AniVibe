package org.opensource.anivibe.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.opensource.anivibe.anime.Result

class SharedAnimeViewModel(application: Application) : AndroidViewModel(application) {

    private val _savedAnimeList = MutableLiveData<List<Result>>(emptyList())
    val savedAnimeList: LiveData<List<Result>> get() = _savedAnimeList

    fun addAnime(anime: Result, context: Context) {
        val currentList = _savedAnimeList.value?.toMutableList() ?: mutableListOf()
        if (!currentList.any { it.malId == anime.malId }) { // Better duplicate check
            currentList.add(anime)
            _savedAnimeList.value = currentList.toList() // Emit new list instance
        }
    }
}