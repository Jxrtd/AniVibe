package org.opensource.anivibe.anime

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.data.SharedAnimeViewModel
import org.opensource.anivibe.databinding.AnimeDetailsBoomSheetBinding
import org.opensource.anivibe.fragments.SaveListFragment
import java.io.Serializable
import java.text.SimpleDateFormat

class AnimeDetailsBottomSheet(private val anime: Result) : BottomSheetDialogFragment() {

    private var _binding: AnimeDetailsBoomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedAnimeViewModel by activityViewModels()

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
            val imageUrl = anime.imageUrl.jpg?.imagesUrl
            if (imageUrl.isNullOrEmpty()) {
                image.setImageResource(R.drawable.moon_icon)
            } else {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.moon_icon)
                    .into(image)
            }

            Log.d("AnimeDetails", "Anime Object: $anime")

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
                anime.url.let { url ->
                    openCustomTab(Uri.parse(url))
                }
            }
        }

        binding.btnSaveAnime.setOnClickListener {
            viewModel.addAnime(anime, requireContext())
            dismiss() // LiveData will automatically update the parent fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCustomTab(url: Uri) {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        builder.build().launchUrl(requireContext(), url)
    }

    private fun formatDate(date: String?): String {
        if (date.isNullOrEmpty()) return "Unknown"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            Log.e("AnimeDetails", "Error formatting date: ${e.message}")
            "Invalid Date"
        }
    }

    companion object {
        fun newInstance(anime: Result): AnimeDetailsBottomSheet {
            return AnimeDetailsBottomSheet(anime)
        }
    }
}
