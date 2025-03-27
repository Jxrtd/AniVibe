package org.opensource.anivibe.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.opensource.anivibe.AnimeList
import org.opensource.anivibe.R
import org.opensource.anivibe.databinding.SaveanimeListBinding

class SaveAnimeListFragment : Fragment(R.layout.saveanime_list) {
    private var _binding: SaveanimeListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SaveanimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Function to reset all button styles to NORMAL
        fun resetButtonStyles() {
            binding.savebtn.setTypeface(null, Typeface.NORMAL) // Reset Save button to normal
            binding.animebtn.setTypeface(null, Typeface.NORMAL) // Reset Anime button to normal
        }

        // Set default style for Save button
        binding.savebtn.setTypeface(null, Typeface.BOLD) // Default bold style for Save button
        replaceFragment(SaveListFragrment())
        // Click listeners
        binding.savebtn.setOnClickListener {
            resetButtonStyles() // Reset styles for all buttons
            binding.savebtn.setTypeface(null, Typeface.BOLD) // Apply bold to Save button
            replaceFragment(SaveListFragrment())
        }

        binding.animebtn.setOnClickListener {
            resetButtonStyles()
            binding.animebtn.setTypeface(null, Typeface.BOLD)

            val intent = Intent(requireContext(), AnimeList::class.java)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
