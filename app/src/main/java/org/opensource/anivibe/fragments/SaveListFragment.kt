package org.opensource.anivibe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import org.opensource.anivibe.R
import org.opensource.anivibe.data.SharedAnimeViewModel
import org.opensource.anivibe.databinding.FragmentSaveListBinding
import org.opensource.anivibe.helper.SaveAdapter

class SaveListFragment : Fragment() {
    private var _binding: FragmentSaveListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedAnimeViewModel
    private lateinit var adapter: SaveAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[SharedAnimeViewModel::class.java]

        setupRecyclerView()
        observeSavedAnimeList()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = SaveAdapter(requireContext(), emptyList())
        binding.recyclerView.adapter = adapter
    }

    private fun observeSavedAnimeList() {
        viewModel.savedAnimeList.observe(viewLifecycleOwner) { animeList ->
            if (animeList.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.updateList(animeList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}