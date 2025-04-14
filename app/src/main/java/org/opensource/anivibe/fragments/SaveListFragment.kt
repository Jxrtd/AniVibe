package org.opensource.anivibe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.R
import org.opensource.anivibe.data.SharedAnimeViewModel
import org.opensource.anivibe.databinding.FragmentSaveListBinding
import org.opensource.anivibe.helper.SaveAdapter

class SaveListFragment : Fragment() {
    private val viewModel: SharedAnimeViewModel by activityViewModels()
    private var _binding: FragmentSaveListBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: SaveAdapter

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

        // Only setup if fragment is active
        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            setupRecyclerView()
            observeViewModel()
        }
    }

    private fun setupRecyclerView() {
        adapter = SaveAdapter(requireContext(), emptyList()).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@SaveListFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.savedAnimeList.observe(viewLifecycleOwner) { savedAnime ->
            Log.d("SAVE_DEBUG", "LiveData emission - Size: ${savedAnime.size}")
            savedAnime.forEachIndexed { index, item ->
                Log.d("SAVE_DEBUG", "Item $index: ${item.title} (Hash: ${item.hashCode()})")
            }

            adapter.updateList(savedAnime)
            binding.emptyState.visibility = if (savedAnime.isEmpty()) View.VISIBLE else View.GONE

            // Force UI update if needed
            binding.recyclerView.post {
                adapter.notifyDataSetChanged()
            }
        }

        // Trigger initial load if needed
        viewModel.savedAnimeList.value?.let { currentList ->
            adapter.updateList(currentList)
        }
    }

    override fun onDestroyView() {
        // Clear references to prevent leaks
        binding.recyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }
}