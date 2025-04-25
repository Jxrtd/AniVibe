package org.opensource.anivibe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import org.opensource.anivibe.anime.SearchedAnime.SearchedAnime
import org.opensource.anivibe.anime.TopAnime.TopAnime
import org.opensource.anivibe.anime.services.AnimeService
import org.opensource.anivibe.databinding.FragmentAnimeListBinding
import org.opensource.anivibe.helper.AnimeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnimeListFragment : Fragment() {
    private var _binding: FragmentAnimeListBinding? = null
    private val binding get() = _binding!!

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

        setupRecyclerView()
        setupButtons()
        loadTopAnime()
    }

    private fun setupRecyclerView() {
        binding.animeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun setupButtons() {

        binding.btnSearch.setOnClickListener {
            val searchedAnime = binding.searchInputEditText.text.toString().trim()
            if (searchedAnime.isNotEmpty()) {
                searchAnime(searchedAnime)
            } else {
                Toast.makeText(requireContext(), "Please enter an anime name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTopAnime() {
        val animeService = AnimeService.create()
        val call = animeService.getTopAnime()

        call.enqueue(object : Callback<TopAnime> {
            override fun onResponse(call: Call<TopAnime>, response: Response<TopAnime>) {
                if (response.isSuccessful && response.body() != null) {
                    val top = response.body()!!.data
                    binding.animeRecyclerView.adapter = AnimeAdapter(requireContext(), top)
                }
            }

            override fun onFailure(call: Call<TopAnime>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun searchAnime(query: String) {
        val animeService = AnimeService.create()
        val callSearchedAnime = animeService.getSearchAnime(query)

        callSearchedAnime.enqueue(object : Callback<SearchedAnime> {
            override fun onResponse(
                call: Call<SearchedAnime>,
                response: Response<SearchedAnime>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val searchedAnimes = response.body()!!.data
                    binding.animeRecyclerView.adapter = AnimeAdapter(requireContext(), searchedAnimes)
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