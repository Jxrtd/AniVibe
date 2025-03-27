package org.opensource.anivibe.anime

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.databinding.AnimeDetailsBoomSheetBinding
import java.text.SimpleDateFormat

class AnimeDetailsBottomSheet(
    private val anime: Result
) : BottomSheetDialogFragment() {

    private var _binding: AnimeDetailsBoomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AnimeDetailsBoomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // Safely load the image URL
            val imageUrl = anime.imageUrl.jpg.imagesUrl

            if (imageUrl.isNullOrEmpty()) {
                image.setImageResource(R.drawable.moon_icon) // Default image
            } else {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.moon_icon)
                    .into(image)
            }

            Log.d("AnimeDetails", "Anime Object: $anime")
            Log.d("AnimeDetails", "Image URL: $imageUrl")

            // Populate other details
            name.text = anime.title ?: "Unknown"
            rating.text = anime.score?.toString() ?: "N/A"
            pgRating.text = anime.rated ?: "Unknown"
            episodes.text = anime.episodes?.let { "$it episodes" } ?: "N/A"
            synopsis.text = anime.synopsis ?: "Synopsis unavailable"

            val startDateFormatted = formatDate(anime.startDate)
            val endDateFormatted = if (anime.endDate.isNullOrEmpty()) {
                "ongoing"
            } else {
                formatDate(anime.endDate)
            }
            dates.text = "$startDateFormatted - $endDateFormatted"

            knowMoreText.setOnClickListener {
                anime.url?.let { url ->
                    openCustomTab(activity as AppCompatActivity, Uri.parse(url))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCustomTab(activity: AppCompatActivity, url: Uri) {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        builder.build().launchUrl(activity, url)
    }

    private fun formatDate(date: String?): String {
        if (date.isNullOrEmpty()) return "Unknown"

        return try {
            if (date.contains("-")) {
                val newDate = date.substring(0, date.lastIndexOf("-"))
                val _date = SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault()).parse(newDate)
                SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault()).format(_date!!)
            } else {
                date
            }
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}
