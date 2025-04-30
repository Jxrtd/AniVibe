package org.opensource.anivibe.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.R
import org.opensource.anivibe.adapters.RecommendationAdapter
import org.json.JSONArray
import java.util.ArrayList

class AnimeStatFragment : Fragment(R.layout.animestat) {

    private lateinit var favoriteAnimeTextView: TextView
    private lateinit var favoriteGenreTextView: TextView
    private lateinit var favoriteCharacterTextView: TextView
    private lateinit var recommendationsRecyclerView: RecyclerView
    private lateinit var emptyRecommendationsText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAnimeTextView = view.findViewById(R.id.favoriteAnimeTextView)
        favoriteGenreTextView = view.findViewById(R.id.favoriteGenreTextView)
        favoriteCharacterTextView = view.findViewById(R.id.favoriteCharacterTextView)
        recommendationsRecyclerView = view.findViewById(R.id.recommendationsRecyclerView)
        emptyRecommendationsText = view.findViewById(R.id.emptyRecommendationsText)

        recommendationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

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

        val recommendationsJson = prefs.getString("recommendations", "[]")
        val recommendations = parseRecommendations(recommendationsJson)

        favoriteAnimeTextView.text = favoriteAnime
        favoriteGenreTextView.text = favoriteGenre
        favoriteCharacterTextView.text = favoriteCharacter

        if (recommendations.isNotEmpty()) {
            recommendationsRecyclerView.adapter = RecommendationAdapter(recommendations)
            recommendationsRecyclerView.visibility = View.VISIBLE
            emptyRecommendationsText.visibility = View.GONE
        } else {
            recommendationsRecyclerView.visibility = View.GONE
            emptyRecommendationsText.visibility = View.VISIBLE
        }
    }

    private fun parseRecommendations(jsonString: String?): List<String> {
        val recommendations = ArrayList<String>()
        if (jsonString.isNullOrEmpty()) return recommendations

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                recommendations.add(jsonArray.getString(i))
            }
        } catch (e: Exception) {
            Log.e("AnimeStatFragment", "Error parsing recommendations: ${e.message}")
        }

        return recommendations
    }
}