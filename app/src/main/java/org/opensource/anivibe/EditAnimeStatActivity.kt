package org.opensource.anivibe

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class EditAnimeStatActivity : AppCompatActivity() {

    // Views
    private lateinit var favoriteAnimeInput: EditText
    private lateinit var favoriteGenreInput: EditText
    private lateinit var favoriteCharacterInput: EditText
    private lateinit var topRecommendationsInput: EditText
    private lateinit var saveButton: MaterialButton
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_anime_stat)

        // Bind views
        favoriteAnimeInput = findViewById(R.id.input_favorite_anime)
        favoriteGenreInput = findViewById(R.id.input_favorite_genre)
        favoriteCharacterInput = findViewById(R.id.input_favorite_character)
        topRecommendationsInput = findViewById(R.id.input_top_recommendations)
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
        topRecommendationsInput.setText(prefs.getString("topRecommendations", ""))
    }

    private fun saveAnimePreferences() {
        val prefs = getSharedPreferences("AnimePreferences", Context.MODE_PRIVATE)

        // Save values
        prefs.edit().apply {
            putString("favoriteAnime", favoriteAnimeInput.text.toString().trim())
            putString("favoriteGenre", favoriteGenreInput.text.toString().trim())
            putString("favoriteCharacter", favoriteCharacterInput.text.toString().trim())
            putString("topRecommendations", topRecommendationsInput.text.toString().trim())
            apply()
        }
    }
}