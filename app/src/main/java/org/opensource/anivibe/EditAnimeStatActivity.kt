package org.opensource.anivibe

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import org.json.JSONException

class EditAnimeStatActivity : AppCompatActivity() {

    // Views
    private lateinit var favoriteAnimeInput: EditText
    private lateinit var favoriteGenreInput: EditText
    private lateinit var favoriteCharacterInput: EditText
    private lateinit var recommendation1Input: EditText
    private lateinit var recommendation2Input: EditText
    private lateinit var recommendation3Input: EditText
    private lateinit var saveButton: MaterialButton
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_anime_stat)

        // Bind views
        favoriteAnimeInput = findViewById(R.id.input_favorite_anime)
        favoriteGenreInput = findViewById(R.id.input_favorite_genre)
        favoriteCharacterInput = findViewById(R.id.input_favorite_character)
        recommendation1Input = findViewById(R.id.input_recommendation_1)
        recommendation2Input = findViewById(R.id.input_recommendation_2)
        recommendation3Input = findViewById(R.id.input_recommendation_3)
        saveButton = findViewById(R.id.button_save)
        backButton = findViewById(R.id.backButton)

        // Handle back button click
        backButton.setOnClickListener {
            finish()
        }

        // Load existing preferences
        loadAnimePreferences()

        // Set up save button
        saveButton.setOnClickListener {
            saveAnimePreferences()
            Toast.makeText(this, "Anime stats updated", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Hide default action bar since we're using custom navigation
        supportActionBar?.hide()
    }

    private fun loadAnimePreferences() {
        val prefs = getSharedPreferences("AnimePreferences", Context.MODE_PRIVATE)

        // Load saved values
        favoriteAnimeInput.setText(prefs.getString("favoriteAnime", ""))
        favoriteGenreInput.setText(prefs.getString("favoriteGenre", ""))
        favoriteCharacterInput.setText(prefs.getString("favoriteCharacter", ""))

        // Load recommendations from JSON using standard JSONArray
        val recommendationsJson = prefs.getString("recommendations", "[]")
        val recommendations = parseRecommendations(recommendationsJson)

        // Set recommendation inputs based on available data
        if (recommendations.isNotEmpty() && recommendations.size >= 1) {
            recommendation1Input.setText(recommendations[0])
        }
        if (recommendations.isNotEmpty() && recommendations.size >= 2) {
            recommendation2Input.setText(recommendations[1])
        }
        if (recommendations.isNotEmpty() && recommendations.size >= 3) {
            recommendation3Input.setText(recommendations[2])
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
            Log.e("EditAnimeStatActivity", "Error parsing recommendations: ${e.message}")
        }

        return recommendations
    }

    private fun saveAnimePreferences() {
        val prefs = getSharedPreferences("AnimePreferences", Context.MODE_PRIVATE)

        // Create a list of non-empty recommendations
        val recommendations = mutableListOf<String>()
        recommendation1Input.text.toString().trim().let { if (it.isNotEmpty()) recommendations.add(it) }
        recommendation2Input.text.toString().trim().let { if (it.isNotEmpty()) recommendations.add(it) }
        recommendation3Input.text.toString().trim().let { if (it.isNotEmpty()) recommendations.add(it) }

        // Convert recommendations to JSON using JSONArray
        val jsonArray = JSONArray()
        for (recommendation in recommendations) {
            jsonArray.put(recommendation)
        }

        // Save values
        prefs.edit().apply {
            putString("favoriteAnime", favoriteAnimeInput.text.toString().trim())
            putString("favoriteGenre", favoriteGenreInput.text.toString().trim())
            putString("favoriteCharacter", favoriteCharacterInput.text.toString().trim())
            putString("recommendations", jsonArray.toString())
            apply()
        }
    }
}