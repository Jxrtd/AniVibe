package org.opensource.anivibe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import org.opensource.anivibe.anime.SearchedAnime.SearchedAnime
import org.opensource.anivibe.anime.TopAnime.TopAnime
import org.opensource.anivibe.anime.services.AnimeService
import org.opensource.anivibe.data.SharedAnimeViewModel
import org.opensource.anivibe.databinding.FragmentAnimeListBinding
import org.opensource.anivibe.helper.AnimeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnimeListFragment : Fragment() {
    private var _binding: FragmentAnimeListBinding? = null
    private val binding get() = _binding!!

    // ViewModel for saved anime state
    private lateinit var viewModel: SharedAnimeViewModel
    // Adapter with dynamic data
    private lateinit var animeAdapter: AnimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity())[SharedAnimeViewModel::class.java]

        // Setup RecyclerView and Adapter
        animeAdapter = AnimeAdapter(requireContext(), emptyList())
        binding.animeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.animeRecyclerView.adapter = animeAdapter

        // Observe saved anime list and update adapter
        viewModel.savedAnimeList.observe(viewLifecycleOwner) { savedList ->
            animeAdapter.setSavedList(savedList)
        }

        setupButtons()
        loadTopAnime()
    }

    private fun setupButtons() {
        binding.btnSearch.setOnClickListener {
            val query = binding.searchInputEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchAnime(query)
            } else {
                Toast.makeText(requireContext(), "Please enter an anime name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTopAnime() {
        AnimeService.create().getTopAnime().enqueue(object : Callback<TopAnime> {
            override fun onResponse(call: Call<TopAnime>, response: Response<TopAnime>) {
                if (response.isSuccessful && response.body() != null) {
                    val topList = response.body()!!.data

                    // Add this debug log
                    for (anime in topList.take(3)) {
                        Log.d("APIResponse", "Anime: ${anime.title}, Image URL: ${anime.imageUrl?.jpg?.imagesUrl}")
                    }

                    animeAdapter.updateList(topList)
                }
            }

            override fun onFailure(call: Call<TopAnime>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun searchAnime(query: String) {
        AnimeService.create().getSearchAnime(query).enqueue(object : Callback<SearchedAnime> {
            override fun onResponse(call: Call<SearchedAnime>, response: Response<SearchedAnime>) {
                if (response.isSuccessful && response.body() != null) {
                    val searchList = response.body()!!.data
                    animeAdapter.updateList(searchList)
                } else {
                    Toast.makeText(requireContext(), "No anime found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchedAnime>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}