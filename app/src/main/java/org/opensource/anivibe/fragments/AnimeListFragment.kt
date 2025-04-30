package org.opensource.anivibe.fragments

import android.os.Bundle
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

    private lateinit var viewModel: SharedAnimeViewModel
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

        viewModel = ViewModelProvider(requireActivity())[SharedAnimeViewModel::class.java]

        setupRecyclerView()

        viewModel.savedAnimeList.observe(viewLifecycleOwner) { savedList ->
            animeAdapter.setSavedList(savedList)
        }

        setupButtons()
        loadTopAnime()
    }

    private fun setupRecyclerView() {
        animeAdapter = AnimeAdapter(requireContext(), emptyList())
        binding.animeRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = animeAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupButtons() {
        binding.btnSearch.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
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
                    animeAdapter.updateList(topList)
                } else {
                    handleApiError("Failed to load top anime")
                }
            }

            override fun onFailure(call: Call<TopAnime>, t: Throwable) {
                handleApiError("Error: ${t.message}")
            }
        })
    }

    private fun searchAnime(query: String) {
        // Show loading indicator if you have one
        AnimeService.create().getSearchAnime(query).enqueue(object : Callback<SearchedAnime> {
            override fun onResponse(call: Call<SearchedAnime>, response: Response<SearchedAnime>) {
                if (response.isSuccessful && response.body() != null) {
                    val searchList = response.body()!!.data
                    if (searchList.isEmpty()) {
                        Toast.makeText(requireContext(), "No anime found for: $query", Toast.LENGTH_SHORT).show()
                    } else {
                        animeAdapter.updateList(searchList)
                    }
                } else {
                    handleApiError("Search failed")
                }
            }

            override fun onFailure(call: Call<SearchedAnime>, t: Throwable) {
                handleApiError("Error: ${t.message}")
            }
        })
    }

    private fun handleApiError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}