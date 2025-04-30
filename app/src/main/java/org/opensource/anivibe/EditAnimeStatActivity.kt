package org.opensource.anivibe

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.json.JSONArray

class EditAnimeStatActivity : AppCompatActivity() {
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

        favoriteAnimeInput = findViewById(R.id.input_favorite_anime)
        favoriteGenreInput = findViewById(R.id.input_favorite_genre)
        favoriteCharacterInput = findViewById(R.id.input_favorite_character)
        recommendation1Input = findViewById(R.id.input_recommendation_1)
        recommendation2Input = findViewById(R.id.input_recommendation_2)
        recommendation3Input = findViewById(R.id.input_recommendation_3)
        saveButton = findViewById(R.id.button_save)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        loadAnimePreferences()

        saveButton.setOnClickListener {
            saveAnimePreferences()
            Toast.makeText(this, "Anime stats updated", Toast.LENGTH_SHORT).show()
            finish()
        }

        supportActionBar?.hide()
    }

    private fun loadAnimePreferences() {
        val prefs = getSharedPreferences("AnimePreferences", Context.MODE_PRIVATE)

        favoriteAnimeInput.setText(prefs.getString("favoriteAnime", ""))
        favoriteGenreInput.setText(prefs.getString("favoriteGenre", ""))
        favoriteCharacterInput.setText(prefs.getString("favoriteCharacter", ""))

        val recommendationsJson = prefs.getString("recommendations", "[]")
        val recommendations = parseRecommendations(recommendationsJson)

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
        } catch (_: Exception) {
        }

        return recommendations
    }

    private fun saveAnimePreferences() {
        val prefs = getSharedPreferences("AnimePreferences", Context.MODE_PRIVATE)

        val recommendations = mutableListOf<String>()
        recommendation1Input.text.toString().trim().let { if (it.isNotEmpty()) recommendations.add(it) }
        recommendation2Input.text.toString().trim().let { if (it.isNotEmpty()) recommendations.add(it) }
        recommendation3Input.text.toString().trim().let { if (it.isNotEmpty()) recommendations.add(it) }

        val jsonArray = JSONArray()
        for (recommendation in recommendations) {
            jsonArray.put(recommendation)
        }

        prefs.edit().apply {
            putString("favoriteAnime", favoriteAnimeInput.text.toString().trim())
            putString("favoriteGenre", favoriteGenreInput.text.toString().trim())
            putString("favoriteCharacter", favoriteCharacterInput.text.toString().trim())
            putString("recommendations", jsonArray.toString())
            apply()
        }
    }
}