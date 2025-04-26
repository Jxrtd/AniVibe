package org.opensource.anivibe.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.opensource.anivibe.R

class AnimeStatFragment : Fragment(R.layout.animestat) {

    private lateinit var favoriteAnimeTextView: TextView
    private lateinit var favoriteGenreTextView: TextView
    private lateinit var favoriteCharacterTextView: TextView
    private lateinit var topRecommendationsTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize TextViews
        favoriteAnimeTextView = view.findViewById(R.id.favoriteAnimeTextView)
        favoriteGenreTextView = view.findViewById(R.id.favoriteGenreTextView)
        favoriteCharacterTextView = view.findViewById(R.id.favoriteCharacterTextView)
        topRecommendationsTextView = view.findViewById(R.id.topRecommendationsTextView)

        // Load anime preferences
        loadAnimePreferences()
    }

    override fun onResume() {
        super.onResume()
        loadAnimePreferences()
    }

    private fun loadAnimePreferences() {
        val prefs = requireContext().getSharedPreferences("AnimePreferences", Context.MODE_PRIVATE)

        val favoriteAnime = prefs.getString("favoriteAnime", "No favorite anime set")
        val favoriteGenre = prefs.getString("favoriteGenre", "No favorite genre set")
        val favoriteCharacter = prefs.getString("favoriteCharacter", "No favorite character set")
        val topRecommendations = prefs.getString("topRecommendations", "No recommendations yet")

        favoriteAnimeTextView.text = favoriteAnime
        favoriteGenreTextView.text = favoriteGenre
        favoriteCharacterTextView.text = favoriteCharacter
        topRecommendationsTextView.text = topRecommendations
    }
}
