package org.opensource.anivibe.anime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.data.SharedAnimeViewModel
import org.opensource.anivibe.databinding.BottomSheetAnimeDetailsBinding

class AnimeDetailsBottomSheet : BottomSheetDialogFragment {
    private var _binding: BottomSheetAnimeDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedAnimeViewModel
    private lateinit var anime: Result

    companion object {
        private const val ARG_ANIME = "anime"

        fun newInstance(anime: Result): AnimeDetailsBottomSheet {
            return AnimeDetailsBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ANIME, anime)
                }
            }
        }
    }

    constructor() : super()

    constructor(anime: Result) : super() {
        arguments = Bundle().apply {
            putParcelable(ARG_ANIME, anime)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            anime = arguments?.getParcelable(ARG_ANIME, Result::class.java)
                ?: throw IllegalStateException("AnimeDetailsBottomSheet: Missing anime argument")
        } else {
            @Suppress("DEPRECATION")
            anime = arguments?.getParcelable(ARG_ANIME)
                ?: throw IllegalStateException("AnimeDetailsBottomSheet: Missing anime argument")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAnimeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[SharedAnimeViewModel::class.java]

        displayAnimeDetails()

        setupActionButton()
    }

    private fun displayAnimeDetails() {
        binding.animeTitle.text = anime.title ?: "Unknown"
        binding.animeDescription.text = anime.synopsis ?: "No description available"

        val imageUrl = anime.imageUrl?.jpg?.imagesUrl
        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.moon_icon)
                .into(binding.animeImage)
        } else {
            binding.animeImage.setImageResource(R.drawable.moon_icon)
        }

        binding.animeRating.text = anime.score?.toString() ?: "N/A"
        binding.animeType.text = anime.type ?: "Unknown"
    }

    private fun setupActionButton() {
        val isSaved = viewModel.isAnimeSaved(anime.malId)

        with(binding.btnSaveAnime) {
            setTextColor(resources.getColor(android.R.color.white, null))
            setOnClickListener {
                viewModel.removeAnime(anime, requireContext())
                Toast.makeText(requireContext(), "${anime.title} removed from your list", Toast.LENGTH_SHORT).show()
                setupActionButton()
            }
            if (isSaved) {
                text = "Remove from list"
            } else {
                text = "Save to list"
                setOnClickListener {
                    viewModel.addAnime(anime, requireContext())
                    Toast.makeText(requireContext(), "${anime.title} saved to your list", Toast.LENGTH_SHORT).show()
                    setupActionButton()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}